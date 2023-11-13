package com.myungwoo.weatherapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitInstance {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // 네트워크 요청을 처리하는 OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }
        .build()

    // Retrofit을 사용하여 HTTP 요청을 처리하는 객체를 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // Retrofit이 생성한 구현체를 사용하여 API 인터페이스의 인스턴스를 생성
    fun createApi(): Api {
        return retrofit.create(Api::class.java)
    }
}