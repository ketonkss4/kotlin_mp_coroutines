package co.happybits.mpcompanion.networking

import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.data.Viewers
import co.happybits.mpcompanion.data.ViewersDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceClientHelper {

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