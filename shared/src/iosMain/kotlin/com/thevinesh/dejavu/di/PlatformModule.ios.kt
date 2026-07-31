package com.thevinesh.dejavu.di

import com.thevinesh.dejavu.platform.IosShareLauncher
import com.thevinesh.dejavu.platform.ShareLauncher
import org.koin.dsl.module

actual fun platformModule() = module {
    single<ShareLauncher> { IosShareLauncher() }
}
