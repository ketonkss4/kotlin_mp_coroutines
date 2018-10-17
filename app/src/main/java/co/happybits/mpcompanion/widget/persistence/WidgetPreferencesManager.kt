package co.happybits.mpcompanion.widget.persistence

interface WidgetPreferencesManager {
    fun saveConvoIdPref(appWidgetId: Int, conversationId: String)
    fun removeConvoIdPref(appWidgetId: Int)
    fun getConvoIdPref(appWidgetId: Int) : String?
}