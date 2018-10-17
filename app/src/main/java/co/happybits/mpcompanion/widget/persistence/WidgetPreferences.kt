package co.happybits.mpcompanion.widget.persistence

import android.content.Context

class WidgetPreferences(private val applicationContext: Context) : WidgetPreferencesManager {

    companion object {
        private const val PREFS_NAME = "co.happybits.mpcompanion.widget.NewAppWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"
    }

    override fun saveConvoIdPref(appWidgetId: Int, conversationId: String) {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, conversationId)
        prefs.apply()
    }

    override fun removeConvoIdPref(appWidgetId: Int) {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.remove(PREF_PREFIX_KEY + appWidgetId)
        prefs.apply()
    }

    override fun getConvoIdPref(appWidgetId: Int): String? {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    }

}