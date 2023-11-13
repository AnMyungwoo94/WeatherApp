package com.myungwoo.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myungwoo.weatherapp.databinding.WeatherItemBinding
import com.myungwoo.weatherapp.network.data.WeekendWeatherData
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherAdapter(val context: Context,  var data: MutableList<WeekendWeatherData>) :
    RecyclerView.Adapter<WeatherAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        val weatherItem = data[position]

        //날짜와 시간 불러오기
        val splitParts = weatherItem.dt_txt.split(" ")
        binding.dayText.text = splitParts[0].substring(5, 10).replace("-", "/")
        binding.timeText.text  = splitParts[1].substring(0, 5)
        //END

        //섭씨온도로 바꿔주기
        val formattedTemp = convertAndFormatTemperature(weatherItem.main.temp)
        binding.tempText.text = "${formattedTemp} ℃"
        //END

        //아이콘 불러오기
        val icon = weatherItem.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/w/$icon.png"
        Glide.with(context)
            .load(iconUrl)
            .error(R.drawable.ic_weather)
            .into(binding.weatherIcon)

    }
    fun updateData(newData: List<WeekendWeatherData>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(val binding: WeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private fun convertAndFormatTemperature(kelvinTemp: Double): String {
    val celsiusTemp = kelvinTemp - 273.15
    return String.format("%.1f", celsiusTemp)
}

