package com.vincorp.dejavu.di

import android.content.Context
import com.vincorp.dejavu.platform.AndroidSoundPlayer
import com.vincorp.dejavu.platform.SoundPlayer
import org.koin.dsl.module

private lateinit var applicationContext: Context

fun setAndroidContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun platformModule() = module {
    single<SoundPlayer> { AndroidSoundPlayer(applicationContext) }
}
