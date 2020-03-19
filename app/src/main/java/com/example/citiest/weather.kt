package com.example.citiest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_weather.*
import org.json.JSONObject
import java.sql.Timestamp
import java.util.*


class weather : AppCompatActivity(), AsyncResponse {

    val TAG = "weatherTAG"

    var dataPoints = arrayOf<DataPoint>()
    var xValues = arrayOf<String>()
    lateinit var graph: GraphView

    val METRIC = "metric"
    val IMPERIAL = "imperial"
    var unit = METRIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val new_unit = intent.getStringExtra("unit")
        if (new_unit == "Metric"){
            unit = METRIC
        } else {
            unit = IMPERIAL
        }
        Log.i("unitTAG", "weather unit is $unit")

        graph = findViewById(R.id.weather_graph)

        val current_loc = TrackLocation(this).getCurrentLocation()
        if (current_loc!=null) {
            val lat = current_loc.latitude
            val lng = current_loc.longitude

            //now, query for weather at this area
            find_current_weather(lat, lng)
            find_weather_forecast(lat, lng)
        }

    }


    fun find_current_weather(lat:Double, lng:Double){
        val baseURL = "http://api.openweathermap.org/data/2.5/weather?"
        val apikey = "e7068701587cd6b5ce49f3c480df3e4a"

        val url = "${baseURL}lat=$lat&lon=$lng&APPID=$apikey"

        val weather = weatherAsync(this).execute(url)

    }

    fun find_weather_forecast(lat:Double, lng:Double){
        val baseURL = "http://api.openweathermap.org/data/2.5/forecast?"
        val apikey = "e7068701587cd6b5ce49f3c480df3e4a"

        val url = "${baseURL}lat=$lat&lon=$lng&APPID=$apikey"

        val weather = weatherAsync(this).execute(url)

    }

    override fun processFinish(output: String?) {
        //implement
        if (output!=null) {
            val jsonObject = JSONObject(output)
            if (jsonObject.has("main")){
                processCurrentWeather(jsonObject)
            } else if (jsonObject.has("list")){
                //Log.i(TAG, "is forecasting")
                processForecast(jsonObject)
            }
        } else {
            Log.i(TAG, "output is null")
        }
    }

    fun processForecast(jsonObject: JSONObject){
        val forecast = jsonObject.getJSONArray("list")
        val length = forecast.length() - 1

        val curr_time = Timestamp(System.currentTimeMillis())

        var hour = 0.0
        var index = 0
        while (hour<=24) {
            val main = forecast.getJSONObject(index).getJSONObject("main")
            val temp = main.getString("temp")

            val celcius = temp.toDouble() - 273.15
            if (unit == METRIC){
                dataPoints+=DataPoint(hour, celcius)
            } else {
                val far = toFar(celcius)
                dataPoints+=DataPoint(hour, far)
            }
            Log.i(TAG, temp.toString())
            hour+=3
            index+=1
        }
        val series = LineGraphSeries(dataPoints)

        //val staticLabelsFormatter = StaticLabelsFormatter(graph)
        //staticLabelsFormatter.setHorizontalLabels(xValues)
        graph.addSeries(series)
        val gridLabel = graph.getGridLabelRenderer()
        if (unit == METRIC){
            gridLabel.verticalAxisTitle = "Temperature in Degrees Celcius"
        } else {
            gridLabel.verticalAxisTitle = "Temperature in Degrees Fahrenheit"
        }
        gridLabel.horizontalAxisTitle = "Temperature for the next 24 hours"

        val viewport = graph.viewport
        viewport.isXAxisBoundsManual = true
        viewport.setMinX(0.0)
        viewport.setMaxX(24+0.1)
    }

    fun convertToTime(unixTime: String): List<String>{
        val date = Date(unixTime.toLong()*1000)
        val time = date.toString().split(" ")
        return time
    }

    fun processCurrentWeather(jsonObject: JSONObject){
        val main = jsonObject.getJSONObject("main")
        val wind = jsonObject.getJSONObject("wind")
        val sys = jsonObject.getJSONObject("sys")
        val weather = jsonObject.getJSONArray("weather")

        val temp = main.getDouble("temp") - 273.15
        Log.i(TAG, "temp: $temp")
        val newtemp = "%.2f".format(temp)
        Log.i(TAG, "temp: $newtemp")
        val wind_speed = wind.getString("speed")
        if (unit==METRIC){
            //temperature
            weather_temp.text = "$newtemp °C"
            //wind speed
            weather_wind_speed.text = String.format("$wind_speed m/s")
        } else {
            //temperature
            val far = toFar(temp)
            val newfar = "%.2f".format(far)
            weather_temp.text = "$newfar °F"

            //wind speed
            val imperial_wind_speed = wind_speed.toDouble()*2.237
            weather_wind_speed.text = String.format("$imperial_wind_speed miles/hr")
        }

        val sunrise = sys.getString("sunrise")
        val sunrise_time = convertToTime(sunrise)
        val date = sunrise_time[0] + ", " + sunrise_time[2] + ", " + sunrise_time[1]
        weather_title.text = "Weather for $date"

        val sunset = sys.getString("sunset")
        val sunset_time = convertToTime(sunset)
        weather_sunrise.text = sunrise_time[3]
        weather_sunset.text = sunset_time[3]


        val description = weather.getJSONObject(0).getString("description")
        Log.i(TAG, "descr = $description")
        weather_description.text = description
    }

    fun toFar(celcius: Double): Double{
        return (celcius*(9/5)+32)
    }

}
