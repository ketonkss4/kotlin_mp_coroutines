package co.happybits.mpcompanion.networking

import co.happybits.mpcompanion.authentication.Auth
import co.happybits.mpcompanion.authentication.dependencies.DaggerAuthComponent
import co.happybits.mpcompanion.data.Viewers
import co.happybits.mpcompanion.data.ViewersDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ServiceClientHelper {
    @Inject
    lateinit var authViewModel: Auth

    init {
        DaggerAuthComponent.builder().build().inject(this)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(loggingInterceptor)
        httpClientBuilder.addInterceptor {
            val request = buildAuthorizedRequest(it.request(), authViewModel.getAuthentication())
            val response = it.proceed(request)
            if (response.code() == 401) {
                runBlocking { authViewModel.reAuthenticate() }
                val reAuthRequest = buildAuthorizedRequest(request, authViewModel.getAuthentication())
                it.proceed(reAuthRequest)
            } else {
                response
            }

        }
        return httpClientBuilder.build()
    }

    fun buildAuthorizedRequest(request: Request, authToken: String): Request {
        return request.newBuilder().addHeader(
                "Authorization",
                authViewModel.getAuthentication()
        ).build()
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