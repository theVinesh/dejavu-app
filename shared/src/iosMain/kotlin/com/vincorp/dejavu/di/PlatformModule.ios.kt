package com.vincorp.dejavu.di

import com.vincorp.dejavu.platform.IosSoundPlayer
import com.vincorp.dejavu.platform.SoundPlayer
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SoundPlayer> { IosSoundPlayer() }
}
