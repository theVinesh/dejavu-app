package in.vincorp.dejavu.di

import in.vincorp.dejavu.platform.IosSoundPlayer
import in.vincorp.dejavu.platform.SoundPlayer
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SoundPlayer> { IosSoundPlayer() }
}
