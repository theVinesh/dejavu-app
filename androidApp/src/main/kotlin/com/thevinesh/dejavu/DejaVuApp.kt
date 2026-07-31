package com.thevinesh.dejavu

import android.app.Application
import com.thevinesh.dejavu.di.initKoin
import com.thevinesh.dejavu.di.setAndroidContext

class DejaVuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setAndroidContext(this)
        initKoin()
    }
}
