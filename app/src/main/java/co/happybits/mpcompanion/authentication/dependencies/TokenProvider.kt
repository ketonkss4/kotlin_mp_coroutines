package co.happybits.mpcompanion.authentication.dependencies

interface TokenProvider {
    fun setApiToken(apiToken: String)
    fun getApiToken() : String
}