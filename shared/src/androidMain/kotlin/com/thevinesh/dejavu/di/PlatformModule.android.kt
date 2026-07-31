package com.thevinesh.dejavu.di

import android.content.Context
import com.thevinesh.dejavu.platform.AndroidShareLauncher
import com.thevinesh.dejavu.platform.ShareLauncher
import org.koin.dsl.module

private lateinit var applicationContext: Context

fun setAndroidContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun platformModule() = module {
    single<ShareLauncher> { AndroidShareLauncher(applicationContext) }
}
