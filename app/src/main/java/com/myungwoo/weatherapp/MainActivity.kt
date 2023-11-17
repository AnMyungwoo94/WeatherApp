package com.myungwoo.weatherapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import android.widget.TextView
import com.myungwoo.weatherapp.databinding.ActivityMainBinding
import com.myungwoo.weatherapp.network.WeatherRepository
import com.myungwoo.weatherapp.network.data.WeatherData
import com.myungwoo.weatherapp.network.data.WeekendWeatherData
import com.myungwoo.weatherapp.widget.WeatherAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val netWorkRepository = WeatherRepository()
    private var weatherData = mutableListOf<WeekendWeatherData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ApiId = arrayOf(
            1835847, 1843561, 1845136, 1843137,
            1835224, 1845106, 1845105, 1835327,
            1845457, 1841066, 1841808, 1838519, 1846265
        )

        //리사이클러뷰 만들기
        val recyclerViewAadapter = WeatherAdapter(this, weatherData)
        binding.recyclerView.adapter = recyclerViewAadapter

        //스피너 만들기
        val spinnerList =
            resources.getStringArray(R.array.area) // res/values/arrays.xml에 정의된 "area" 배열에서 지역명을 가져와서 리스트로 만든다.
        val spinnerItems =
            spinnerList.map { SpinnerData(it) } // 각 지역명을 SpinnerData 객체로 매핑하여 리스트를 생성한다.
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            R.id.textView,
            spinnerList
        ) // ArrayAdapter를 생성하여 스피너에 사용할 데이터와 레이아웃을 설정한다.
        binding.searchSpinner.adapter = adapter // 생성한 어댑터를 스피너에 연결합니다.
        binding.searchSpinner.setSelection(0) // 스피너 처음값 지정

        binding.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItemId = ApiId[position] //선택된 API ID값

                val selectedText = spinnerItems[position].text //선택된 텍스트값
                val textView = view?.findViewById<TextView>(R.id.textView)  //스피너 텍스트 업데이트
                textView?.text = selectedText

                adapter.notifyDataSetChanged() //데이터 변경사항 알려주기

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        //선택된 selectedItemId 값으로 API 불러오기 즉, 지역별 ID값으로 불러오는것.
                        val weatherCurrentList = netWorkRepository.getCurrentList(selectedItemId)
//                        Log.e("날씨 데이터", weatherCurrentList.toString())
                        val forecastWeatherData = netWorkRepository.getWeekendList(selectedItemId)

                        //리사이클러뷰에 뿌려줄 Data를 weekendData 넣어주기
                        val weekendData = forecastWeatherData.list
                        recyclerViewAadapter.updateData(weekendData)

                        //스피너 클릭시 지역별로 최근날씨 변경해주기
                        updateCurrentList(weatherCurrentList)

                        // 위젯 만들기 위해서 intent로 전달해줌
                        createWidget(weatherCurrentList)

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

    private fun updateCurrentList(weatherCurrentList: WeatherData) {
        //섭씨온도로 바꿔주기
        binding.tempText.text = convertFormatTemperature(weatherCurrentList.main.temp)
        binding.tempmaxText.text = convertFormatTemperature(weatherCurrentList.main.temp_max)
        binding.tempminText.text = convertFormatTemperature(weatherCurrentList.main.temp_min)
        //END

        //날짜와 시간 바꿔주기
        val date = Date(weatherCurrentList.dt * 1000L)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val formattedTime = SimpleDateFormat("a hh:mm", Locale.getDefault()).format(date)
        binding.dayText.text = formattedDate
        binding.timeText.text = formattedTime
        //END

        //강수량, 습도, 풍속 바꿔주기
        binding.rainText.text = "${weatherCurrentList.clouds.all}%"
        binding.humidityText.text = "${weatherCurrentList.main.humidity}%"
        val windSpeedInKmPerH = weatherCurrentList.wind.speed * 3.6
        val formattedWindSpeed = String.format("%.2f", windSpeedInKmPerH)
        binding.windText.text = "${formattedWindSpeed} km/h"
        //END
    }

    private fun convertFormatTemperature(kelvinTemp: Double): String {
        return String.format("%.1f ℃", kelvinTemp - 273.15)
    }

    private fun getFormattedDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    private fun getFormattedTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("a hh:mm", Locale.getDefault())
        return format.format(date)
    }

    private fun createWidget(weatherCurrentList: WeatherData) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, WeatherAppWidgetProvider::class.java))

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(packageName, R.layout.widget_weather)
            views.setTextViewText(R.id.tempTextView, convertFormatTemperature(weatherCurrentList.main.temp))
            views.setTextViewText(R.id.dayTextView, getFormattedDate(weatherCurrentList.dt))
            views.setTextViewText(R.id.timeTextView, getFormattedTime(weatherCurrentList.dt))

            // 위젯 업데이트
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
