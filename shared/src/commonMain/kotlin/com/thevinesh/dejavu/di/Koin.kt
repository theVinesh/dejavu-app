package com.thevinesh.dejavu.di

import com.thevinesh.dejavu.data.TutorialPreferences
import com.thevinesh.dejavu.domain.GameSession
import com.thevinesh.dejavu.screens.step.StepViewModel
import com.thevinesh.dejavu.screens.word.WordViewModel
import com.thevinesh.dejavu.screens.wordcount.WordCountViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    singleOf(::TutorialPreferences)
    singleOf(::GameSession)
    viewModelOf(::WordCountViewModel)
    viewModelOf(::StepViewModel)
    viewModelOf(::WordViewModel)
}

expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule, platformModule())
    }
}
