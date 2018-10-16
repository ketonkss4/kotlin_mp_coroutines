package co.happybits.mpcompanion.networking

import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.data.Response
import co.happybits.mpcompanion.data.Viewers
import co.happybits.mpcompanion.data.ViewersDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

class ServiceClientHelper {

    interface PoloService {
        @GET("conversations/sync")
        fun requestConversationSync(): Deferred<Response>

        @PUT("conversations/{conversation_id}/messages/{message_id}")
        fun sendMessage(
                @Path("conversation_id") conversationId: String,
                @Path("message_id") messageId: String,
                @Body emotiveMessage : String
        ) : Deferred<ResponseBody>
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(loggingInterceptor)
        httpClientBuilder.addInterceptor {
            val request = it.request().newBuilder().addHeader(
                    "Authorization",
                    MpCompanion.instance.authToken
            ).build()
            it.proceed(request)
        }
        return httpClientBuilder.build()
    }

    fun buildService(): PoloService {
        val gson = GsonBuilder()
                .registerTypeAdapter(Viewers::class.java, ViewersDeserializer())
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getOkHttpClient())
                .build()
        return retrofit.create(PoloService::class.java)
    }
}