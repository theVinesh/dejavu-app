import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

private val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
private val androidReleaseSigningEnvVars = listOf(
    "ANDROID_RELEASE_KEYSTORE_PATH",
    "ANDROID_RELEASE_KEYSTORE_PASSWORD",
    "ANDROID_RELEASE_KEY_ALIAS",
    "ANDROID_RELEASE_KEY_PASSWORD",
)
private val hasAndroidReleaseSigning = androidReleaseSigningEnvVars.all { !System.getenv(it).isNullOrBlank() }
private val hasAnyAndroidReleaseSigning = androidReleaseSigningEnvVars.any { !System.getenv(it).isNullOrBlank() }

check(!hasAnyAndroidReleaseSigning || hasAndroidReleaseSigning) {
    "Set all Android release signing env vars or none of them: ${androidReleaseSigningEnvVars.joinToString()}"
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.foundation)
}

android {
    namespace = "com.thevinesh.dejavu"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.thevinesh.dejavu"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = githubRunNumber
        versionName = "1.0.$githubRunNumber"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        if (hasAndroidReleaseSigning) {
            create("release") {
                storeFile = file(System.getenv("ANDROID_RELEASE_KEYSTORE_PATH")!!)
                storePassword = System.getenv("ANDROID_RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_RELEASE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = if (hasAndroidReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
