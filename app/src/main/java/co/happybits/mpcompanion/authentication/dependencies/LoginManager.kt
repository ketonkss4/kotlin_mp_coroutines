package co.happybits.mpcompanion.authentication.dependencies

import android.content.Context
import android.os.Build
import android.util.Base64
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.networking.API_BASE_URL
import co.happybits.mpcompanion.networking.PlatformHttpConnection
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import org.json.JSONObject
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CancellationException

class LoginManager private constructor() {

    private val _deviceID: String?
    private val _secret: String?

    private object Holder {
        val INSTANCE = LoginManager()
    }

    companion object {
        val instance: LoginManager by lazy { Holder.INSTANCE }
        /**
         * Creates a new XID based on a random UUID (uuid4).
         */
        fun createXID(): String {
            val uuid = UUID.randomUUID()

            val buffer = ByteBuffer.wrap(ByteArray(16))
            buffer.putLong(uuid.mostSignificantBits)
            buffer.putLong(uuid.leastSignificantBits)

            // Return only the first 22 characters.
            return Base64.encodeToString(buffer.array(), Base64.URL_SAFE).substring(0, 22)
        }
    }

    init {
        val context = MpCompanion.instance.applicationContext
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        _deviceID = prefs.getString("DeviceID", createXID())
        _secret = prefs.getString("AuthSecret", createXID())
    }

    @Throws(IOException::class)
    fun auth(): Deferred<String> {
        val deferred = CompletableDeferred<String>()
        try {
            val authParams = JSONObject()
                    .put("device_id", _deviceID)
                    .put("secret", _secret)
                    .put("app_type", "mp")
                    .put("app_version", "1.0.0")
                    .put("app_build", "1000")
                    .put("platform_type", "android")
                    .put("platform_version", "24")
                    .put("manufacturer", Build.MANUFACTURER)
                    .put("model_name", Build.MODEL)
                    .put("flavor", "dev")
                    .put("locale", "en_US")
                    .put("timezone", "PST")

            val connection = PlatformHttpConnection("${API_BASE_URL}auth")
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            val requestCall = connection.getRequest(PlatformHttpConnection.POST, headers, authParams)
            deferred.invokeOnCompletion { requestCall.cancel() }

            val response = requestCall.execute()
            if (!response.isSuccessful) {
                deferred.cancel(CancellationException("Authorization Failed : ${response.code()}"))
            } else {
                val responseJSON = JSONObject(response.body()?.bytes()?.let { String(it) })
                deferred.complete(responseJSON.getString("api_token"))
            }
        } catch (ex: Exception) {
            deferred.cancel(CancellationException(ex.message))
        }
        return deferred
    }

    fun login(phone: String, countryCode: String, authToken: String): Deferred<String> {
        val deferred = CompletableDeferred<String>()
        // phone: the local phone number including area code (e.g. 2065551234)
        // countryCode: the two letter country identifier (e.g. "US")
        try {
            val code = getAutoVerifyPhoneCode(
                    phone,
                    countryCode,
                    authToken,
                    deferred
            )
            code?.let {
                verifyCode(
                        phone,
                        countryCode,
                        code,
                        authToken,
                        deferred
                )
            }
        } catch (ex: IOException) {
            deferred.cancel(ex)
        }
        return deferred
    }

    @Throws(IOException::class)
    private fun getAutoVerifyPhoneCode(
            phone: String,
            countryCode: String,
            authToken: String,
            deferred: CompletableDeferred<String>
    ): String? {

        try {
            val params = JSONObject()
                    .put("phone", phone)
                    .put("country_code", countryCode)

            val responseJSON = doAuthenticatedRequest(
                    PlatformHttpConnection.POST,
                    "admin/user/phone_code",
                    params,
                    authToken,
                    deferred
            )

            if (responseJSON.has("code")) {
                return responseJSON.getString("code")
            }
        } catch (ex: Exception) {
            deferred.cancel(CancellationException(ex.message))
        }

        return null
    }

    @Throws(IOException::class)
    private fun verifyCode(
            phone: String,
            countryCode: String,
            code: String?,
            authToken: String,
            deferred: CompletableDeferred<String>
    ) {
        try {
            val params = JSONObject()
                    .put("phone", phone)
                    .put("country_code", countryCode)
                    .put("verification_code", code)

            val responseJSON = doAuthenticatedRequest(
                    PlatformHttpConnection.POST,
                    "auth/verify-phone-code",
                    params,
                    authToken,
                    deferred
            )
            if(responseJSON.has("api_token")) {
                deferred.complete(responseJSON.getString("api_token"))
            }
        } catch (ex: Exception) {
            deferred.cancel(CancellationException("Phone Verification Failed: ${ex.message}"))
        }
    }

    @Throws(IOException::class)
    private fun doAuthenticatedRequest(
            method: String,
            path: String,
            params: JSONObject,
            authToken: String,
            deferred: Deferred<String>
    ): JSONObject {
        return try {
            val connection = PlatformHttpConnection(API_BASE_URL + path)
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer $authToken"
            val requestCall = connection.getRequest(method, headers, params)
            deferred.invokeOnCompletion { requestCall.cancel() }
            val response = requestCall.execute()
            val jsonObject = JSONObject(response.body()?.bytes()?.let { String(it) })
            if(jsonObject.has("error")) {
                val errorMsg = jsonObject.getJSONObject("error").getString("message")
                deferred.cancel(CancellationException("Verification Failed : $errorMsg"))
            }
            jsonObject
        } catch (ex: Exception) {
            JSONObject()
        }
    }
}

suspend fun <T> Deferred<T>.awaitAuthResult(): AuthResult<T> = try {
    AuthResult(true, await(), null)
} catch (e: Exception) {
    AuthResult(false, null, e)
}

data class AuthResult<out T>(val isSuccessful: Boolean, val result: T?, val exception: Exception?)