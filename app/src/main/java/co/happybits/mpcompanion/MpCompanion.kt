package co.happybits.mpcompanion

import android.app.Application
import co.happybits.mpcompanion.injection.DaggerAppComponent

class MpCompanion : Application() {

    companion object {

        @Volatile
        lateinit var instance: MpCompanion
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        instance = this
    }
}
