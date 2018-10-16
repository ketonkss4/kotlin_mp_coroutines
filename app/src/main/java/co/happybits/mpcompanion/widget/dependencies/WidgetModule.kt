package co.happybits.mpcompanion.widget.dependencies

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.widget.WidgetViewModel
import co.happybits.mpcompanion.widget.persistence.WidgetPreferences
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import dagger.Module
import dagger.Provides

@Module
open class WidgetModule {
    @Provides
    open fun provideWidgetViewModel(
            poloService: ServiceClientHelper.PoloService,
            dispatchers: KtDispatchers,
            widgetPreferencesManager: WidgetPreferencesManager
    ): WidgetViewModel {
        return WidgetViewModel(
                poloService,
                dispatchers,
                MutableLiveData(),
                widgetPreferencesManager
        )
    }

    @Provides
    fun provideWidgetPreferencesManager(): WidgetPreferencesManager {
        return WidgetPreferences(MpCompanion.instance)
    }
}