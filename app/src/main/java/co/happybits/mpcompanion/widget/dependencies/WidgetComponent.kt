package co.happybits.mpcompanion.widget.dependencies

import co.happybits.mpcompanion.injection.AppComponent
import co.happybits.mpcompanion.injection.AppModule
import co.happybits.mpcompanion.widget.WidgetViewModel
import dagger.Component
import javax.inject.Singleton

@WidgetScope @Component(dependencies = [AppComponent::class], modules = [WidgetModule::class])
interface WidgetComponent {
    val widgetViewModel: WidgetViewModel
    fun inject(widgetViewController: WidgetViewController)
}