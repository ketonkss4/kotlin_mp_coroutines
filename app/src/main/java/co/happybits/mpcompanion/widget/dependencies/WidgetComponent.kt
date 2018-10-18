package co.happybits.mpcompanion.widget.dependencies

import co.happybits.mpcompanion.authentication.Auth
import co.happybits.mpcompanion.authentication.dependencies.AuthModule
import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.widget.WidgetConfigureActivity
import co.happybits.mpcompanion.widget.WidgetViewProvider
import co.happybits.mpcompanion.widget.WidgetViewModel
import dagger.Component

@WidgetScope
@Component(dependencies = [AppComponent::class], modules = [WidgetModule::class, AuthModule::class])
interface WidgetComponent {
    val widgetViewModelViewModel: WidgetViewModel
    val authViewModel: Auth
    fun inject(widgetViewProvider: WidgetViewProvider)
    fun inject(widgetConfigureActivity: WidgetConfigureActivity)
}