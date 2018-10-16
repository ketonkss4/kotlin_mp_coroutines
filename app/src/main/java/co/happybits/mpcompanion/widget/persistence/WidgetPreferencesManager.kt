package co.happybits.mpcompanion.widget.persistence

interface WidgetPreferencesManager {
    fun saveConvoIdPref(appWidgetId: Int, conversationId: String)
    fun getConvoIdPref(appWidgetId: Int) : String?
}