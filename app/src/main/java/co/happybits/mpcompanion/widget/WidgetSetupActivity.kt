package co.happybits.mpcompanion.widget

/**
 * Configuration screen for a widget already placed on the homescreen that needs setup
 */

class WidgetSetupActivity : WidgetConfigureActivity() {
    override fun completeConfiguration() {
        finish()
    }
}