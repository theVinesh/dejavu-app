package com.thevinesh.dejavu.di

import com.russhwolf.settings.Settings
import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.screens.step.StepViewModel
import com.thevinesh.dejavu.screens.word.WordViewModel
import com.thevinesh.dejavu.screens.wordcount.WordCountViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    singleOf(::TutorialPreferences)
    singleOf(::GameSession)
    viewModelOf(::WordCountViewModel)
    viewModelOf(::StepViewModel)
    viewModelOf(::WordViewModel)
}

expect fun platformModule(): Module

fun initKoin() {
    startKoin {
        modules(appModule, platformModule())
    }
}
