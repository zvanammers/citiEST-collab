package com.example.citiest

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.city_view.*

class city_view : AppCompatActivity(){
    lateinit var citysights : Button
    lateinit var cityname : TextView
    lateinit var introduction: TextView
    lateinit var sight1 : Button
    lateinit var sight2 : Button
    lateinit var sight3 : Button
    lateinit var weather_city : Button
    var names : ArrayList<String> = arrayListOf()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_view)
        val typeface = Typeface.createFromAsset(assets, "Aino_31_170411-Regular.otf")
        city_background_image.setImageResource(R.drawable.estonia_picture)

        val intent= intent
        val city= intent.getStringExtra( "name")
        val index= intent.getIntExtra( "Index",0 )
        Log.i("debug index","$index")
        val contents=resources.getStringArray(R.array.city_introductions)



        cityname=findViewById(R.id.city_name)

        cityname.text=city
        cityname.typeface=typeface




        introduction=findViewById(R.id.introduction)
        introduction.text=contents[index]
        introduction.typeface=typeface
        introduction.bringToFront()

        citysights=findViewById(R.id.allsight)
        citysights.typeface=typeface
        citysights.setOnClickListener{
            val maps_intent =Intent(this,MapsActivity::class.java)
            val index=3
            Log.i("debug", "city is AAAAAAA")
            if (city!=null) {
                Log.i("debug", "city is $city")
            } else {
                Log.i("debug","Write city name in city view")
            }
            maps_intent.putExtra("sight_clicked",index)
            maps_intent.putExtra("city_name",city)
            startActivity(maps_intent)
        }



        if (city.equals("Tallinn")){sightsInfo(resources.getStringArray(R.array.Tallinn))}
        else if (city.equals("Tartu")) {sightsInfo(resources.getStringArray(R.array.Tartu))}
        else if (city.equals("Narva")) {sightsInfo(resources.getStringArray(R.array.Narva))}
        else if (city.equals("Pärnu")) {sightsInfo(resources.getStringArray(R.array.Pärnu))}
        else if (city.equals("Viljandi")) {sightsInfo(resources.getStringArray(R.array.Viljandi))}
        else if (city.equals("Rakvere")) {sightsInfo(resources.getStringArray(R.array.Rakvere))}
        else if (city.equals("Võru")) {sightsInfo(resources.getStringArray(R.array.Võru))}

        sight1 =findViewById(R.id.sight1)
        sight1.typeface=typeface
        sight1.text=names[0]
        sight1.setOnClickListener{
            val maps_intent =Intent(this, MapsActivity::class.java)
            val index=0
            maps_intent.putExtra("sight_clicked",index)
            if (city!=null) {
                Log.i("debug", "city is $city")
            } else {
                Log.i("debug","Write city name in city view")
            }
            maps_intent.putExtra("city_name",city)
            maps_intent.putExtra("city_name",city)
            startActivity(maps_intent)
        }





        sight2 =findViewById(R.id.sight2)
        sight2.typeface=typeface
        sight2.text=names[1]
        sight2.setOnClickListener{
            val maps_intent =Intent(this,MapsActivity::class.java)
            val index=1
            maps_intent.putExtra("sight_clicked",index)
            maps_intent.putExtra("city_name",city)
            startActivity(maps_intent)
        }



        sight3 =findViewById(R.id.sight3)
        sight3.typeface=typeface
        sight3.text=names[2]
        sight3.setOnClickListener{
            val maps_intent =Intent(this,MapsActivity::class.java)
            val index=2
            maps_intent.putExtra("sight_clicked",index)
            maps_intent.putExtra("city_name",city)
            startActivity(maps_intent)
        }










    }
    private fun sightsInfo(info:Array<String>){
        names.clear()

        for (i in 0 until info.size) {
            var pieces = info[i].split(",")
            names.add(pieces[0])
        }
    }
}