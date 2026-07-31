package com.thevinesh.dejavu.di

import android.content.Context
import com.thevinesh.dejavu.platform.AndroidSoundPlayer
import com.thevinesh.dejavu.platform.SoundPlayer
import org.koin.dsl.module

private lateinit var applicationContext: Context

fun setAndroidContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun platformModule() = module {
    single<SoundPlayer> { AndroidSoundPlayer(applicationContext) }
}
