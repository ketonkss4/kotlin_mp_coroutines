package co.happybits.mpcompanion

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.injection.DaggerAppComponent

class MpCompanion : Application() {
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
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
    }

    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
