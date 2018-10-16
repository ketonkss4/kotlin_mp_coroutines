package co.happybits.mpcompanion.widget.dependencies

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.widget.WidgetViewModel
import dagger.Module
import dagger.Provides

@Module class WidgetModule {
    @Provides fun provideWidgetViewModel(poloService: ServiceClientHelper.PoloService
    , dispatchers: KtDispatchers): WidgetViewModel {
        return WidgetViewModel(
                poloService,
                dispatchers,
                MutableLiveData()
        )
    }
}