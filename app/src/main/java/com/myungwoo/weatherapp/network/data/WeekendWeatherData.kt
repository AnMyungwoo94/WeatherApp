package com.myungwoo.weatherapp.network.data

data class WeatherForecast(
    val cod: String,
    val message: Double,
    val cnt: Int,
    val list: List<WeekendWeatherData>
)

data class WeekendWeatherData(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherDetail>,
    val clouds: CloudData,
    val wind: WindData,
    val visibility: Double,
    val pop: Double,
    val sys: SysData,
    val dt_txt: String
)

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class WeatherDetail(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class CloudData(
    val all: Int
)

data class WindData(
    val speed: Double,
    val deg: Double,
    val gust: Double
)

data class SysData(
    val pod: String
)