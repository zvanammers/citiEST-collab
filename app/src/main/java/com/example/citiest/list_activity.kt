package com.example.citiest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.widget.*
import kotlinx.android.synthetic.main.activity_list.*


class list_activity : AppCompatActivity() {
    lateinit var citieslist: ListView
    lateinit var uiname: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        background_image.setImageResource(R.drawable.estonia_picture)

        val typeface = Typeface.createFromAsset(assets, "Aino_31_170411-Regular.otf")
        uiname=findViewById(R.id.ui_name)
        uiname.typeface=typeface


        citieslist=findViewById(R.id.cities_list)
        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,resources.getStringArray(R.array.city_names))

        citieslist.setAdapter(adapter)


        citieslist.setOnItemClickListener{
                parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent = Intent(this, city_view::class.java)
            val cityname = citieslist.getItemAtPosition(position).toString()
            val index =position
            Log.i("debug position","$position")
            intent.putExtra("name",cityname)
            intent.putExtra("Index",index)
            startActivityForResult(intent,1)


        }





    }




}

