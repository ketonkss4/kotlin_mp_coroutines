package co.happybits.mpcompanion.authentication.dependencies.persistence

import android.content.Context

class AuthPreferences(private val applicationContext: Context) : AuthPreferencesManager {
    companion object {
        private const val PREFS_NAME = "co.happybits.mpcompanion.authentication.API_TOKEN"
        private const val AUTH_PREFS_KEY = "API_AUTHORIZATION_KEY"
        private const val PHONE_AUTH_PREFS_KEY = "PHONE_AUTH_PREFS_KEY"
    }

     override fun saveAuthPref(apiToken: String, phoneNumber: String) {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putString(AUTH_PREFS_KEY, apiToken)
        prefs.putString(PHONE_AUTH_PREFS_KEY, phoneNumber)
        prefs.apply()
    }

     override fun removeAuthPref() {
         applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

     override fun getAuthPref(): String? {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(AUTH_PREFS_KEY, null)
    }

    override fun getPhoneAuthPref(): String? {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PHONE_AUTH_PREFS_KEY, null)
    }
}