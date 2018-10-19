package co.happybits.mpcompanion.widget.dependencies

import android.content.Context
import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.widget.WidgetViewModel
import co.happybits.mpcompanion.widget.persistence.WidgetPreferences
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides

@Module
open class WidgetModule {
    @Provides
    open fun provideWidgetViewModel(
            poloService: PoloService,
            dispatchers: KtDispatchers,
            widgetPreferencesManager: WidgetPreferencesManager,
            authViewModelViewModel: AuthViewModel
    ): WidgetViewModel {
        return WidgetViewModel(
                poloService,
                dispatchers,
                MutableLiveData(),
                widgetPreferencesManager,
                authViewModelViewModel,
                Picasso.get()
        )
    }

    @Provides
    fun provideWidgetPreferencesManager(context: Context): WidgetPreferencesManager {
        return WidgetPreferences(context)
    }
}