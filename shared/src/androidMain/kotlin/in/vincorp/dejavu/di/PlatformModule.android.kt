package in.vincorp.dejavu.di

import android.content.Context
import in.vincorp.dejavu.platform.AndroidSoundPlayer
import in.vincorp.dejavu.platform.SoundPlayer
import org.koin.dsl.module

private lateinit var applicationContext: Context

fun setAndroidContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun platformModule() = module {
    single<SoundPlayer> { AndroidSoundPlayer(applicationContext) }
}
