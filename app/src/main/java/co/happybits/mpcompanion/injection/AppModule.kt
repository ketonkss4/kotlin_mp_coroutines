package co.happybits.mpcompanion.injection

import android.content.Context
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.networking.ServiceClientHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    fun provideContext(app: MpCompanion): Context = app.applicationContext

    @Singleton
    @Provides
    fun providePoloService(): PoloService = ServiceClientHelper().buildService()
}