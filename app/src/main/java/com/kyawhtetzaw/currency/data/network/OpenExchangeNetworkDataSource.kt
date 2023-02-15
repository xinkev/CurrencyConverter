package com.kyawhtetzaw.currency.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kyawhtetzaw.currency.BaseCurrency
import com.kyawhtetzaw.currency.BuildConfig
import com.kyawhtetzaw.currency.data.network.model.NetworkExchangeRates
import com.kyawhtetzaw.currency.data.network.model.NetworkResponse
import javax.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private interface OpenExchangeApi {
    @GET("latest.json")
    suspend fun latest(
        @Query("base") baseCurrency: String = "USD"
    ): NetworkResponse<NetworkExchangeRates>
}

class OpenExchangeNetworkDataSource @Inject constructor(json: Json) : NetworkDataSource {
    private val baseUrl = "https://openexchangerates.org/api/"

    private val keyInterceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Token ${BuildConfig.OPEN_EXCHANGE_KEY}")
            .build()
        chain.proceed(request)
    }

    private val api = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(buildOkhttpClient())
        .addConverterFactory(
            @OptIn(ExperimentalSerializationApi::class)
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(OpenExchangeApi::class.java)

    private fun buildOkhttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }
        builder.addInterceptor(keyInterceptor)
        return builder.build()
    }

    override suspend fun latest(): NetworkExchangeRates {
        return api.latest(baseCurrency = BaseCurrency).rates
    }

}