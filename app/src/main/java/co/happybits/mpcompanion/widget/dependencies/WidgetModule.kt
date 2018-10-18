package co.happybits.mpcompanion.widget.dependencies

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.widget.WidgetViewModel
import co.happybits.mpcompanion.widget.persistence.WidgetPreferences
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import dagger.Module
import dagger.Provides

@Module
open class WidgetModule {
    @Provides
    open fun provideWidgetViewModel(
            poloService: PoloService,
            dispatchers: KtDispatchers,
            widgetPreferencesManager: WidgetPreferencesManager,
            authViewModel: AuthViewModel
    ): WidgetViewModel {
        return WidgetViewModel(
                poloService,
                dispatchers,
                MutableLiveData(),
                widgetPreferencesManager,
                authViewModel
        )
    }

    @Provides
    fun provideWidgetPreferencesManager(): WidgetPreferencesManager {
        return WidgetPreferences(MpCompanion.instance)
    }
}