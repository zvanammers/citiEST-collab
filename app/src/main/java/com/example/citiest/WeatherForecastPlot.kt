package com.example.citiest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.sql.Timestamp

class WeatherForecastPlot(context: Context, attrs: AttributeSet): View(context, attrs) {

    var dataSetMin = 0.0f
    var dataSet = mutableListOf<Float>()
    var dataSet24Hours = mutableListOf<Float>()
    var dataSet24HoursLength = 0
    val temperatures = mutableListOf<Float>()
    var dataSetMax = 20f
    var dataSetLength = 0
    var start_time = ""
    var end_time = ""
    val curr_time = System.currentTimeMillis()

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 8f
    }

    private val redPaint = Paint().apply {
        color = Color.RED
        textSize = 32f
    }

    private val blackPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
    }

    fun addToList(temp: Float){
        dataSet.add(temp)
        dataSetLength+=1
        if (dataSet24HoursLength<8){
            dataSet24Hours.add(temp)
            dataSet24HoursLength +=1
        }
    }

    fun is_relevent_time(time: String): Boolean{
        return (time.toLong() > curr_time)
    }

    fun renewGraph(){
        dataSetMin = dataSet24Hours.min()!! - 2
        dataSet24Hours.forEachIndexed { index, value ->
            temperatures.add(dataSet24Hours[index] - 273.15f)
            dataSet24Hours[index] = dataSet24Hours[index] - dataSetMin
        }
        dataSetMax = dataSet24Hours.max()!! + 1
        /*dataSetMin = dataSet.min()!! - 2
        dataSet.forEachIndexed { index, value ->
            temperatures.add(dataSet[index] - 273.15f)
            dataSet[index] = dataSet[index] - dataSetMin
        }
        dataSetMax = dataSet.max()!! + 1*/
        invalidate()
    }

    /** Helper functions which scale the numeric values to screen dimensions */
    private fun scaledX(value : Float) : Float { return ((value / (dataSet24Hours.size+2)) * width)}
    // Y- value is also reversed because 0,0 coordinate is in top-left corner in Canvas
    private fun scaledY(value : Float) : Float { return (height - ((value / dataSetMax) * height) )}

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var pastx = 0
        var pasty = 0f
        var newx: Int
        var newy: Float


        dataSet24Hours.forEachIndexed { i, value ->
            val x = i+1
            canvas?.drawPoint(scaledX(x.toFloat()), scaledY(value), bluePaint)
            val t = temperatures[i]
            //canvas?.drawText(t.toString(), scaledX(x.toFloat()), scaledY(value), redPaint)
            if (i == 0){
                pastx = x
                pasty = value
                canvas?.drawLine(scaledX(0.5f), scaledY(dataSet24Hours.max()!!+0.5f), scaledX(0.5f), scaledY(dataSet24Hours.min()!!-0.5f), blackPaint)
                canvas?.drawLine(scaledX(0.5f), scaledY(dataSet24Hours.min()!!-0.5f), scaledX(dataSet24HoursLength-1f), scaledY(dataSet24Hours.min()!!-0.5f), blackPaint)
                var t = temperatures[0]
                canvas?.drawText(t.toString(), scaledX(x.toFloat()), scaledY(value), redPaint)
                t = temperatures.min()!!
                canvas?.drawText(t.toString(), scaledX(x.toFloat()), scaledY(dataSet.min()!!), redPaint)

            } else{
                newx = x
                newy = value
                canvas?.drawLine(scaledX(pastx.toFloat()), scaledY(pasty), scaledX(newx.toFloat()), scaledY(newy), bluePaint)
                pastx = newx
                pasty = newy
            }

        }
    }
}