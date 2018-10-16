package co.happybits.mpcompanion.widget.dependencies

import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.authentication.dependencies.AuthModule
import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.widget.WidgetConfigureActivity
import co.happybits.mpcompanion.widget.WidgetViewController
import co.happybits.mpcompanion.widget.WidgetViewModel
import dagger.Component

@WidgetScope
@Component(dependencies = [AppComponent::class], modules = [WidgetModule::class, AuthModule::class])
interface WidgetComponent {
    val widgetViewModel: WidgetViewModel
    val authViewModel: AuthViewModel
    fun inject(widgetViewController: WidgetViewController)
    fun inject(widgetConfigureActivity: WidgetConfigureActivity)
}