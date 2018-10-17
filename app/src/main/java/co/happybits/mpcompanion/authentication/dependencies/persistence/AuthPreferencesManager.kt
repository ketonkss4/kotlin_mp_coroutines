package co.happybits.mpcompanion.authentication.dependencies.persistence

interface AuthPreferencesManager {
    fun saveAuthPref(apiToken: String, phoneNumber: String)
    fun removeAuthPref()
    fun getAuthPref(): String?
    fun getPhoneAuthPref(): String?
}
fun AuthPreferencesManager.hasSavedAuth() : Boolean {
    return getAuthPref()!= null
}