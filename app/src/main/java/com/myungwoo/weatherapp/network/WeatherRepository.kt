package com.myungwoo.weatherapp.network

class WeatherRepository {
    private val apiKey: String = "4aacca01c8811753ae2b87c2bdb2cd41" // 여기에 실제 API 키를 넣어주세요
    private val client = RetrofitInstance.createApi()

    suspend fun getCurrentList(id: Int) = client.getCurrentList(id, apiKey)

    suspend fun getWeekendList(id: Int) = client.getWeekendList(id, apiKey)

}