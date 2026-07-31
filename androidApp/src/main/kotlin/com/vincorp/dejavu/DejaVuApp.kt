package com.vincorp.dejavu

import android.app.Application
import com.vincorp.dejavu.di.initKoin
import com.vincorp.dejavu.di.setAndroidContext

class DejaVuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setAndroidContext(this)
        initKoin()
    }
}
