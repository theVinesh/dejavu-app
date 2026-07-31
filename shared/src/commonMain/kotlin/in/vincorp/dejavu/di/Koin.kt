package in.vincorp.dejavu.di

import in.vincorp.dejavu.data.TutorialPreferences
import in.vincorp.dejavu.domain.GameSession
import in.vincorp.dejavu.screens.step.StepViewModel
import in.vincorp.dejavu.screens.word.WordViewModel
import in.vincorp.dejavu.screens.wordcount.WordCountViewModel
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
