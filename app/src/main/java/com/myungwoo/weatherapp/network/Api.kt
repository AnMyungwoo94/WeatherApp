package com.myungwoo.weatherapp.network

import com.myungwoo.weatherapp.network.data.WeatherData
import com.myungwoo.weatherapp.network.data.WeatherForecast
import com.myungwoo.weatherapp.network.data.WeekendWeatherData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface  Api {

    @GET("weather")
    suspend fun getCurrentList(
        @Query("id") id: Int,
        @Query("appid") apiKey: String
    ): WeatherData

    @GET("forecast")
    suspend fun getWeekendList(
        @Query("id") id: Int,
        @Query("appid") apiKey: String
    ): WeatherForecast

}
