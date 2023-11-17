package com.myungwoo.weatherapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.myungwoo.weatherapp.MainActivity
import com.myungwoo.weatherapp.R
import com.myungwoo.weatherapp.network.WeatherRepository
import com.myungwoo.weatherapp.network.data.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch MainActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
                }

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_weather
            ).apply {
                setOnClickPendingIntent(R.id.tempTextView, pendingIntent)
                setTextViewText(R.id.tempTextView, "") // 초기에는 텍스트뷰를 비워줌
                setTextViewText(R.id.dayTextView, "날짜")
                setTextViewText(R.id.timeTextView, "시간")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context!!,
                    WeatherAppWidgetProvider::class.java
                )
            )
            val weatherData = intent.getParcelableArrayListExtra<WeatherData>("weatherData")
            updateWidget(context, appWidgetManager, appWidgetIds, weatherData)
        }
    }

    private fun updateWidget(
        context: Context?,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        weatherData: ArrayList<WeatherData>?
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context?.packageName, R.layout.widget_weather)
            if (!weatherData.isNullOrEmpty()) {
                views.setTextViewText(
                    R.id.tempTextView,
                    convertFormatTemperature(weatherData[0].main.temp)
                )
                views.setTextViewText(
                    R.id.dayTextView,
                    getFormattedDate(weatherData[0].dt)
                )

                views.setTextViewText(
                    R.id.timeTextView,
                    getFormattedTime(weatherData[0].dt)
                )
            } else {
                views.setTextViewText(R.id.tempTextView, "") // 텍스트뷰를 비워줌
                views.setTextViewText(R.id.dayTextView, "") // 텍스트뷰를 비워줌
                views.setTextViewText(R.id.timeTextView, "") // 텍스트뷰를 비워줌
            }

            // 위젯 업데이트
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun convertFormatTemperature(kelvinTemp: Double): String {
        // Implement your temperature conversion logic here
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
}
