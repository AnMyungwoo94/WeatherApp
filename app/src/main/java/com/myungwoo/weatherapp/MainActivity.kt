package com.myungwoo.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.weatherapp.databinding.ActivityMainBinding
import com.myungwoo.weatherapp.network.WeatherRepository
import com.myungwoo.weatherapp.network.data.WeekendWeatherData
import com.myungwoo.weatherapp.spinner.SpinnerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val netWorkRepository = WeatherRepository()
    var weatherData = mutableListOf<WeekendWeatherData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerItemIds = arrayOf(
            1835847, 1843561, 1845136, 1843137,
            1835224, 1845106, 1845105, 1835327,
            1845457, 1841066, 1841808, 1838519, 1846265
        )

        //리사이클러뷰 만들기
        val recyclerViewAadapter = WeatherAdapter(this, weatherData)
        binding.recyclerView.adapter = recyclerViewAadapter

        //스피너 만들기
        var spinnerList = resources.getStringArray(R.array.area)
        val spinnerItems = spinnerList.map { SpinnerData(it) }
        Log.e("spinnerList", spinnerList[5].toString())
        val adapter = ArrayAdapter(this, R.layout.spinner_item, R.id.textView, spinnerList)
        binding.searchSpinner.adapter = adapter
        binding.searchSpinner.setSelection(0) // 스피너 처음값 지정

        binding.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //아이템 선택 잘 되는지 확인하는 로그
                Log.d("ItemSelected", "Item selected at position $position")

                val selectedItemId = spinnerItemIds[position]
                val selectedText = spinnerItems[position].text
                Log.e("selectedText", selectedText)

                //스피너 텍스트 업데이트
                val textView = view?.findViewById<TextView>(R.id.textView)
                textView?.text = selectedText

                //데이터 변경사항 알려주기
                adapter.notifyDataSetChanged()

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val currentList = netWorkRepository.getCurrentList(selectedItemId)
                       val  weatherData = netWorkRepository.getWeekendList(selectedItemId)
                        val weekendData = weatherData.list
                        recyclerViewAadapter.updateData(weekendData)
//                        Log.e("selectedText", currentList.toString())
//                        Log.e("selectedText", weatherData.toString())


                        //섭씨온도로 바꿔주기
                        val formattedTemp = convertAndFormatTemperature(currentList.main.temp)
                        binding.tempText.text = "${formattedTemp} ℃"
                        val formattedTempMax =
                            convertAndFormatTemperature(currentList.main.temp_max)
                        binding.tempmaxText.text = "${formattedTempMax} ℃"
                        val formattedTempMin =
                            convertAndFormatTemperature(currentList.main.temp_min)
                        binding.tempminText.text = "${formattedTempMin} ℃"
                        //END

                        //날짜와 시간 바꿔주기
                        val date = Date(currentList.dt * 1000L)
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val timeFormatter = SimpleDateFormat("a hh:mm", Locale.getDefault())
                        val formattedDate = dateFormatter.format(date)
                        val formattedTime = timeFormatter.format(date)
                        binding.dayText.text = formattedDate
                        binding.timeText.text = formattedTime
                        Log.e("formattedTime", formattedTime.toString())
                        //END

                        binding.rainText.text = "${currentList.clouds.all}%"
                        binding.humidityText.text = "${currentList.main.humidity}%"
                        val windSpeedInKmPerH = currentList.wind.speed * 3.6
                        val formattedWindSpeed = String.format("%.2f", windSpeedInKmPerH)
                        binding.windText.text = "${formattedWindSpeed} km/h"


                    } catch (e: Exception) {
                        // Handle network errors
                        Log.e("NetworkError", e.message ?: "Unknown error")
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
                TODO("Not yet implemented")
            }
        }
    }


    private fun convertAndFormatTemperature(kelvinTemp: Double): String {
        val celsiusTemp = kelvinTemp - 273.15
        return String.format("%.1f", celsiusTemp)
    }

}
