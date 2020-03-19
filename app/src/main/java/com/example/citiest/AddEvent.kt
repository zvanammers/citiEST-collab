package com.example.citiest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

import kotlinx.android.synthetic.main.activity_add_event.*
import java.lang.Exception

class AddEvent : AppCompatActivity() {

    val RESULT_OK = 1
    var editing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        var id = ""

        //receive intent extras if editing
        if (intent.hasExtra("title")){
            editing = true
            add_event.text = "Edit Event"
            addevent_finish_button.text = "Edit Event"
            if (intent.hasExtra("id")){
                id = intent.getStringExtra("id")

            }
            //place values into the textviews
            val title = intent.getStringExtra("title")
            val loc = intent.getStringExtra("loc")
            val desc = intent.getStringExtra("desc")
            val date = intent.getStringExtra("date")
            if (title!=null && loc!=null && desc!=null && date!=null) {
                val split_date = date.split(".")
                add_event_day_text.setText(split_date[0])
                add_event_month_text.setText(split_date[1])
                add_event_year_text.setText(split_date[2])
                add_event_title_view.setText(title)
                add_event_location_view.setText(loc)
                add_event_description.setText(desc)
            }
        } else {
            //hide delete button when making event
            delete_button.visibility = View.INVISIBLE
        }

        delete_button.setOnClickListener(){
            val returnIntent = Intent()
            returnIntent.putExtra("delete", "delete")
            returnIntent.putExtra("id", id)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        addevent_exit_button.setOnClickListener{
            finish()
        }

        addevent_finish_button.setOnClickListener{

            val day = add_event_day_text.text.toString()
            val month = add_event_month_text.text.toString()
            val year = add_event_year_text.text.toString()
            //check validity of date
            val valid = check_date_validity(day, month, year)
            //if not valid, don't finish
            //if valid, get date value
            if (valid){
                val date = getDate(day, month, year)

                val title = add_event_title_view.text
                val city = add_event_location_view.text
                val desc = add_event_description.text

                Log.i("weatherTAG", "$title, $city, $desc")

                val returnIntent = Intent()
                returnIntent.putExtra("date", date)
                returnIntent.putExtra("title", title.toString())
                returnIntent.putExtra("location", city.toString())
                returnIntent.putExtra("desc", desc.toString())

                if (editing){
                    returnIntent.putExtra("id", id)
                }

                setResult(RESULT_OK, returnIntent)
                finish()
            } else {
                val toast = Toast.makeText(applicationContext, "Invalid Date", Toast.LENGTH_SHORT)
                toast.show()
            }


        }
    }

    private fun check_date_validity(day: String, month: String, year: String): Boolean{
        //check type of values
        try {
            val day_int = day.toInt()
            val month_int = month.toInt()
            val year_int = year.toInt()
            //then, check range of values
            return ((day_int in 1..31) && (month_int in 1..12) && (year_int in 0..3000))
        } catch (e: Exception) {
            return false
        }
    }

    private fun getDate(day: String, month: String, year: String): String{
        var date = ""
        if (day.length!=2){
            date = "0$day"
        } else {
            date = "$day"
        }
        if (month.length!=2){
            date += ".0$month"
        } else {
            date+= ".$month"
        }
        val year_length = year.length
        val extra_year_nums = 4 - year_length
        var new_year = "."
        if (year.length != 4){
            new_year += "0".repeat(extra_year_nums)
            new_year += year
        } else {
            new_year += year
        }
        date+=new_year

 //       val date = "$day.$month.$year"
        return date
    }

}
