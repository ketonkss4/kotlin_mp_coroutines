package co.happybits.mpcompanion.widget.dependencies

import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.authentication.dependencies.AuthModule
import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.widget.WidgetConfigureActivity
import co.happybits.mpcompanion.widget.WidgetViewModel
import co.happybits.mpcompanion.widget.WidgetViewProvider
import dagger.Component

@WidgetScope
@Component(dependencies = [AppComponent::class], modules = [WidgetModule::class, AuthModule::class])
interface WidgetComponent {
    val widgetViewModel: WidgetViewModel
    val authViewModelViewModel: AuthViewModel
    fun inject(widgetViewProvider: WidgetViewProvider)
    fun inject(widgetConfigureActivity: WidgetConfigureActivity)
}