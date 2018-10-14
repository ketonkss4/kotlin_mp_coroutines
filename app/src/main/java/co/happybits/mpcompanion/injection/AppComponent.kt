package co.happybits.mpcompanion.injection

import co.happybits.mpcompanion.MpCompanion
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MpCompanion): Builder

        fun build(): AppComponent
    }

    fun inject(app: MpCompanion)
}