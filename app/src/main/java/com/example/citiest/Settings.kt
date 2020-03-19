package com.example.citiest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*

class Settings: AppCompatActivity() {

    val RETURN_OKAY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val unit = intent.getStringExtra("unit")
        if (unit!=null){
            if (unit == "Metric"){
                Metric.isChecked = true
            } else {
                imperial.isChecked = true
            }
        }

        saveButton.setOnClickListener(){
            val returnIntent = Intent()
            if (Metric.isChecked){
                returnIntent.putExtra("Units", "Metric")
                Log.i("unitTAG", "in settings: Metric")
            } else {
                returnIntent.putExtra("Units", "Imperial")
                Log.i("unitTAG", "in settings: Imperial")
            }
            if (checkBox.isChecked){
                returnIntent.putExtra("clearEvents", "clearEvents")
            }
            setResult(2, returnIntent)
            finish()
        }

    }
}