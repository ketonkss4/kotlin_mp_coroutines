package co.happybits.mpcompanion.networking

import okhttp3.*
import org.json.JSONObject

import java.io.IOException
import java.nio.ByteBuffer
import java.util.HashMap

import okio.ByteString

class PlatformHttpConnection @JvmOverloads constructor(private val _uri: String, private val _session: PlatformHttpSession = PlatformHttpSession.sharedSession()) {

    @Throws(IOException::class)
    fun buildRequest(
            method: String,
            headers: HashMap<String, String>?,
            body: ByteBuffer?
    ): Call {
        val client = _session.client
        val bodyString = if (body != null) ByteString.of(body) else ByteString.EMPTY
        val requestBody = RequestBody.create(null, bodyString)
        val request = createRequest(_uri, method, headers, requestBody)
        return client.newCall(request)
    }

    @Throws(IOException::class)
    fun getRequest(
            method: String,
            headers: HashMap<String, String>,
            jsonBody: JSONObject
    ): Call {
        val body = jsonBody.toString().toByteArray(charset("UTF-8"))
        return buildRequest(method, headers, ByteBuffer.wrap(body))
    }

    companion object {

        val GET = "GET"
        val POST = "POST"

        private fun createRequest(uri: String, method: String, headers: HashMap<String, String>?, requestBody: RequestBody): Request {
            var builder: Request.Builder = Request.Builder().url(uri)

            // Can't pass a null body to method() for GET.
            if (method == GET) {
                builder = builder.get()
            } else {
                builder = builder.method(method, requestBody)
            }

            // Can't pass null headers to Headers.of() or headers().
            if (headers != null) {
                builder = builder.headers(Headers.of(headers))
            }

            return builder.build()
        }

    }
}
