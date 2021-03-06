package co.happybits.mpcompanion.injection

import android.content.Context
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.widget.WidgetService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ConcurrencyModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MpCompanion): Builder

        fun build(): AppComponent
    }

    fun applicationContext(): Context
    fun service(): PoloService
    fun dispatchers(): KtDispatchers
    fun inject(app: MpCompanion)
    fun inject(app: WidgetService)
}