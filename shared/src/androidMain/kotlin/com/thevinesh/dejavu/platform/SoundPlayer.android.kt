package com.thevinesh.dejavu.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.thevinesh.dejavu.shared.R

class AndroidSoundPlayer(
    context: Context
) : SoundPlayer {
    private val appContext = context.applicationContext
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var bounceSoundId: Int = 0
    @Volatile
    private var isLoaded = false
    @Volatile
    private var playWhenLoaded = false

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
                if (playWhenLoaded) {
                    playWhenLoaded = false
                    playLoadedSound()
                }
            }
        }
        bounceSoundId = soundPool.load(appContext, R.raw.bounce, 1)
    }

    override fun playBounce() {
        if (!isLoaded) {
            playWhenLoaded = true
            return
        }
        playLoadedSound()
    }

    private fun playLoadedSound() {
        soundPool.play(bounceSoundId, 1f, 1f, 1, 0, 1f)
    }

    override fun release() {
        soundPool.release()
    }
}
