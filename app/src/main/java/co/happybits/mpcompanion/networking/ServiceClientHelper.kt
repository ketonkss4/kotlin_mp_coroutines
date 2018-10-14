package co.happybits.mpcompanion.networking

import co.happybits.mpcompanion.data.Conversation
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

class ServiceClientHelper {

    interface PoloService {
        @GET("/conversations/sync")
        fun requestConversationSync(): Deferred<List<Conversation>>

        @PUT(" /conversations/{conversation_id}/messages/{message_id}")
        fun sendMessage(@Path("conversation_id") conversationId: String, @Path("message_id") messageId: String)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(loggingInterceptor)
        return httpClientBuilder.build()
    }

    fun buildService(): PoloService {
        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build()
        return retrofit.create(PoloService::class.java)
    }
}