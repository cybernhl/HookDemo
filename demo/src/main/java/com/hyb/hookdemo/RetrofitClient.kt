package com.hyb.hookdemo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://newsapi.org/v2/"

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val okhttpClient = OkHttpClient.Builder()
//                val levelType = HttpLoggingInterceptor.Level.BODY
//
//                val logging = HttpLoggingInterceptor()
//                logging.setLevel(levelType)
//                okhttpClient.addInterceptor(logging)
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okhttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    @JvmStatic
    val newsApiService: NewsApiService
        get() = retrofitInstance!!.create(NewsApiService::class.java)
}
