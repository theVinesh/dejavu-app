#!/usr/bin/env bash
#
# Capture and stage store screenshots for both platforms.
#
#   ./scripts/store_screenshots.sh capture-ios      # boot simulators, run Maestro
#   ./scripts/store_screenshots.sh capture-android  # resize connected device, run Maestro
#   ./scripts/store_screenshots.sh stage            # copy captures into Fastlane paths
#   ./scripts/store_screenshots.sh all              # all of the above
#
# Raw captures land in store_assets/screenshots/<platform>/<form-factor>/ and are
# copied from there into the Fastlane metadata paths that supply and deliver read.
#
# Every store size is captured natively rather than resized afterwards: both
# stores reject off-spec pixel dimensions, and rescaling a screenshot to fit
# either distorts the aspect ratio or softens the text. `stage` re-checks the
# dimensions so an off-spec capture fails here instead of mid-upload.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RAW="$ROOT/store_assets/screenshots"
FLOW="$ROOT/maestro/store_screenshots.yaml"
PLAY_IMAGES="$ROOT/androidApp/fastlane/metadata/android/en-GB/images"
IOS_SHOTS="$ROOT/iosApp/fastlane/screenshots/en-US"

export PATH="$HOME/.maestro/bin:$PATH"

# App Store Connect only requires the largest display class per device family and
# scales the rest down, so 6.9" iPhone plus 13" iPad covers every iOS size. These
# simulators report exactly those resolutions.
IOS_PHONE_DEVICE="${IOS_PHONE_DEVICE:-iPhone 17 Pro Max}"   # 1320x2868, 6.9" class
IOS_TABLET_DEVICE="${IOS_TABLET_DEVICE:-iPad Pro 13-inch (M4)}" # 2064x2752, 13" class

log() { printf '\n=== %s\n' "$*"; }
die() { printf 'error: %s\n' "$*" >&2; exit 1; }

png_size() {
  sips -g pixelWidth -g pixelHeight "$1" | awk '/pixelWidth/{w=$2} /pixelHeight/{h=$2} END{print w "x" h}'
}

require_size() {
  local file="$1" expected="$2" actual
  actual="$(png_size "$file")"
  [ "$actual" = "$expected" ] || die "$file is ${actual}, expected ${expected}"
}

# Matched on the exact device name - the plain-text listing cannot be parsed
# reliably because names such as "iPad Pro 13-inch (M4)" contain parentheses.
# Prefers the newest runtime when a device exists on several.
simulator_udid() {
  xcrun simctl list devices available -j | python3 -c '
import json, sys

want = sys.argv[1]
devices = json.load(sys.stdin)["devices"]
for runtime in sorted(devices, reverse=True):
    for device in devices[runtime]:
        if device["name"] == want:
            print(device["udid"])
            sys.exit(0)
' "$1"
}

run_flow() {
  local device="$1" dest="$2"
  rm -rf "$dest" && mkdir -p "$dest"
  ( cd "$dest" && maestro --device "$device" test "$FLOW" )
  ls "$dest" | sed 's/^/  /'
}

ios_app_path() {
  if [ -n "${IOS_APP_PATH:-}" ]; then
    printf '%s' "$IOS_APP_PATH"
    return
  fi
  local derived="$ROOT/build/screenshot-derived"
  local app="$derived/Build/Products/Debug-iphonesimulator/DejaVu.app"
  if [ ! -d "$app" ]; then
    log "Building DejaVu.app for the simulator"
    xcodebuild -project "$ROOT/iosApp/iosApp.xcodeproj" -scheme iosApp \
      -sdk iphonesimulator -configuration Debug -derivedDataPath "$derived" \
      build >/dev/null
  fi
  [ -d "$app" ] || die "no simulator build at $app; set IOS_APP_PATH"
  printf '%s' "$app"
}

capture_ios() {
  local app
  app="$(ios_app_path)"

  local name device_name udid
  for entry in "iphone69:$IOS_PHONE_DEVICE" "ipad13:$IOS_TABLET_DEVICE"; do
    name="${entry%%:*}"
    device_name="${entry#*:}"
    udid="$(simulator_udid "$device_name")"
    [ -n "$udid" ] || die "no available simulator named '$device_name'"

    log "Capturing iOS $name on $device_name"
    xcrun simctl boot "$udid" >/dev/null 2>&1 || true
    xcrun simctl bootstatus "$udid" -b >/dev/null
    xcrun simctl install "$udid" "$app"
    run_flow "$udid" "$RAW/ios/$name"
  done
}

capture_android() {
  local device
  device="$(adb devices | awk 'NR>1 && $2=="device" {print $1; exit}')"
  [ -n "$device" ] || die "no connected Android device or running emulator"

  local apk="$ROOT/androidApp/build/outputs/apk/debug/androidApp-debug.apk"
  if [ ! -f "$apk" ]; then
    log "Building debug APK"
    ( cd "$ROOT" && ./gradlew :androidApp:assembleDebug >/dev/null )
  fi
  adb -s "$device" install -r "$apk" >/dev/null

  # Play caps the long side at twice the short side, so a stock 20:9 phone
  # screenshot is rejected. Driving the display to Play's recommended sizes
  # captures a correctly laid out screen at a legal aspect ratio instead of
  # cropping or letterboxing one afterwards. Densities keep the layout in the
  # dp range each form factor is designed for (phone ~411dp, 7" 600dp, 10" 800dp).
  local name size density
  for entry in "phone:1080x1920:420" "tablet7:1200x1920:320" "tablet10:1600x2560:320"; do
    IFS=: read -r name size density <<<"$entry"
    log "Capturing Android $name at $size (${density}dpi)"
    adb -s "$device" shell wm size "$size" >/dev/null
    adb -s "$device" shell wm density "$density" >/dev/null
    sleep 3
    run_flow "$device" "$RAW/android/$name"
  done

  adb -s "$device" shell wm size reset >/dev/null
  adb -s "$device" shell wm density reset >/dev/null
}

# Copy the captures of one form factor into an already-empty destination,
# renaming them to "<prefix>1.png", "<prefix>2.png", ... Both stores order
# screenshots by file name, so the capture order is what the listing shows.
stage_set() {
  local src="$1" dest="$2" prefix="$3" expected="$4"
  [ -d "$src" ] || die "missing captures in $src - run the capture step first"
  compgen -G "$src/store_*.png" >/dev/null || die "no store_*.png captures in $src"

  local index=1 file
  for file in "$src"/store_*.png; do
    require_size "$file" "$expected"
    cp "$file" "$dest/$prefix$index.png"
    index=$((index + 1))
  done
  printf '  %s -> %d screenshot(s) at %s\n' "${dest#$ROOT/}" "$((index - 1))" "$expected"
}

reset_dir() {
  rm -rf "$1" && mkdir -p "$1"
}

stage() {
  log "Staging App Store screenshots"
  # deliver picks the display class from the pixel dimensions, not the file name.
  # The prefixes are deliver's own display-type constants, which still carry the
  # older 6.7"/12.9" labels for what Apple now calls the 6.9" and 13" classes.
  reset_dir "$IOS_SHOTS"
  stage_set "$RAW/ios/iphone69" "$IOS_SHOTS" "APP_IPHONE_67_" "1320x2868"
  stage_set "$RAW/ios/ipad13" "$IOS_SHOTS" "APP_IPAD_PRO_3GEN_129_" "2064x2752"

  log "Staging Play Store screenshots"
  for entry in "phone:phoneScreenshots:1080x1920" \
               "tablet7:sevenInchScreenshots:1200x1920" \
               "tablet10:tenInchScreenshots:1600x2560"; do
    IFS=: read -r name folder expected <<<"$entry"
    reset_dir "$PLAY_IMAGES/$folder"
    stage_set "$RAW/android/$name" "$PLAY_IMAGES/$folder" "" "$expected"
  done

  log "Staging Play Store graphics"
  # supply expects these as files directly under images/ - nesting them in
  # images/icon/icon.png makes it skip them silently, with no upload and no error.
  stage_graphic "$ROOT/store_assets/android/icon-512.png" "$PLAY_IMAGES/icon.png" "512x512"
  stage_graphic "$ROOT/store_assets/android/feature-graphic-1024x500.png" "$PLAY_IMAGES/featureGraphic.png" "1024x500"
}

stage_graphic() {
  local src="$1" dest="$2" expected="$3"
  [ -f "$src" ] || die "missing $src"
  require_size "$src" "$expected"
  cp "$src" "$dest"
  printf '  %s at %s\n' "${dest#$ROOT/}" "$expected"
}

case "${1:-all}" in
  capture-ios) capture_ios ;;
  capture-android) capture_android ;;
  stage) stage ;;
  all) capture_ios; capture_android; stage ;;
  *) die "usage: $0 {capture-ios|capture-android|stage|all}" ;;
esac

log "Done"
