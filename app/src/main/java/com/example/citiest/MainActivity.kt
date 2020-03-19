package com.example.citiest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.sql.Timestamp
import java.util.EnumSet.range

class MainActivity : AppCompatActivity(), AsyncResponse {

    var estonia_map = false
    val PERMISSION_ACCEPTED = 1
    val TAG = "cityTAG"
    val reqCode = 2
    var unit = "Metric"
    val UNIT_PREFERENCE = "UNIT_PREFERENCE"
    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var db: EventDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, EventDatabase::class.java, "my_event_database")
            .allowMainThreadQueries()
            .build()

        background_image.setImageResource(R.drawable.swamp1)
        val typeface = Typeface.createFromAsset(assets, "Aino_31_170411-Regular.otf")
        current_city_textview_main.typeface = typeface
        current_city_textview_main.bringToFront()

        weather_button_main.typeface = typeface

        cities_button_main.typeface = typeface
        my_events_button_main.typeface = typeface
        change_image_button.typeface=typeface
        textView2.typeface=typeface

        sp = getSharedPreferences(UNIT_PREFERENCE, Context.MODE_PRIVATE)
        editor = sp.edit()
        val old_unit = sp.getString("unit", "false")
        if (old_unit == "false"){
        } else {
            if (old_unit!=null){
                unit = old_unit
            }
        }



        change_background_image()

        change_image_button.setOnClickListener{
            change_background_image()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1 )
        } else {
            //permission granted
            //get location
            //use rest of app
            set_up_app_functionality()

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            PERMISSION_ACCEPTED -> {
                //permission was granted
                //display contacts
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    set_up_app_functionality()
                } else {
                    val duration = Toast.LENGTH_LONG
                    val toast = Toast.makeText(applicationContext, "There are invalid permissions for this app to operate", duration)
                    toast.show()
                }
            }
        }
    }


    fun change_background_image(){
        if (estonia_map){
            background_image.setImageResource(R.drawable.swamp1)
            estonia_map = false
        } else {
            background_image.setImageResource(R.drawable.estonia_picture2)
            estonia_map = true
        }
    }

    fun set_up_app_functionality(){

        searchForCity()

        weather_button_main.setOnClickListener{
            val weather_intent = Intent(this, weather::class.java)
            weather_intent.putExtra("unit", unit)
            startActivity(weather_intent)
        }
        my_events_button_main.setOnClickListener{
            val event_intent = Intent(this, MyEvents::class.java)
            startActivity(event_intent)
        }
        cities_button_main.setOnClickListener{
            val city_intent = Intent(this, list_activity::class.java)

            startActivity(city_intent)
        }
    }

    fun searchForCity(){
        val current_loc = TrackLocation(this).getCurrentLocation()
        if (current_loc!=null) {
            val lat = current_loc.latitude
            val lng = current_loc.longitude

            Log.i(TAG, "current location is $lat, $lng")
            //now, query for the current city
            val baseURL = "http://api.openweathermap.org/data/2.5/forecast?"
            val apikey = "e7068701587cd6b5ce49f3c480df3e4a"

            val url = "${baseURL}lat=$lat&lon=$lng&APPID=$apikey"

            val city = CityAsync(this).execute(url)
        }
    }

    override fun processFinish(output: String?) {
        if (output!=null) {
            val jsonObject = JSONObject(output)
            if (jsonObject.has("list")){
                findCity(jsonObject)
            }
        } else {
            Log.i(TAG, "output is null")
        }
    }

    fun findCity(jsonObject: JSONObject){
        val cityDetails = jsonObject.getJSONObject("city")
        val cityName = cityDetails.getString("name")
        current_city_textview_main.text = cityName
    }
    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.settings_button) {
            // start setting activity
            Log.i("cityTAG", "clicked settings button")
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("unit", unit)
            startActivityForResult(intent, reqCode)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==reqCode){
            if (data!=null){
                if (data.hasExtra("Units")){
                    unit = data.getStringExtra("Units")
                    editor.putString("unit", unit)
                    editor.commit()
                }
                if (data.hasExtra("clearEvents")){
                    db.eventDao().deleteAllEvents()
                }
            }
        }
    }
}

