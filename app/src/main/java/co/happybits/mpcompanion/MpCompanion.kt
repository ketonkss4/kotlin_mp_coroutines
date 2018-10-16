package co.happybits.mpcompanion

import android.app.Application
import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.injection.DaggerAppComponent

class MpCompanion : Application() {
    var authToken: String = ""
    companion object {

        @Volatile
        lateinit var instance: MpCompanion
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        appComponent.inject(this)
        instance = this
    }
}