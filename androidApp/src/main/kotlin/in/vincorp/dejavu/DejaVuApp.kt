package in.vincorp.dejavu

import android.app.Application
import in.vincorp.dejavu.di.initKoin
import in.vincorp.dejavu.di.setAndroidContext

class DejaVuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setAndroidContext(this)
        initKoin()
    }
}
