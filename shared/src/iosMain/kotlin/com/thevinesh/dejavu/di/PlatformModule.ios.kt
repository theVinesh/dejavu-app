package com.thevinesh.dejavu.di

import com.thevinesh.dejavu.platform.IosSoundPlayer
import com.thevinesh.dejavu.platform.SoundPlayer
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SoundPlayer> { IosSoundPlayer() }
}
