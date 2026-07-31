package com.thevinesh.dejavu.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IosSoundPlayer : SoundPlayer {
    private var player: AVAudioPlayer? = null

    init {
        runCatching {
            AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryAmbient, error = null)
        }
        prepare()
    }

    private fun prepare() {
        val url: NSURL = NSBundle.mainBundle.URLForResource("bounce", withExtension = "mp3")
            ?: return
        player = AVAudioPlayer(contentsOfURL = url, error = null)?.apply {
            prepareToPlay()
        }
    }

    override fun playBounce() {
        val audioPlayer = player ?: run {
            prepare()
            player
        } ?: return
        audioPlayer.currentTime = 0.0
        audioPlayer.play()
    }

    override fun release() {
        player?.stop()
        player = null
    }
}
