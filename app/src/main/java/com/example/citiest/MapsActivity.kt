package com.example.citiest


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import kotlin.math.absoluteValue

//import sun.invoke.util.VerifyAccess.getPackageName



class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    override fun onMyLocationClick(location: Location) {
        var latitude = location.latitude
        var longitude = location.longitude
        var nLocation = LatLng(latitude, longitude)

        val geocoder = Geocoder(this)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val loc = addresses[0].getAddressLine(0)

        mMap.addMarker(MarkerOptions().position(nLocation).title("You are here $loc"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nLocation,15F))
    }

    private lateinit var mMap: GoogleMap

    var names : ArrayList<String> = arrayListOf()
    var coord : ArrayList<LatLng> = arrayListOf()
    var coord_string : ArrayList<String> = arrayListOf()
    var duration : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        Ion.getDefault(applicationContext).getConscryptMiddleware().enable(false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val index = intent.getIntExtra("sight_clicked", -1)
        val city=intent.getStringExtra("city_name")
        if (city!=null) {
            Log.i("debug", "city is $city")
            Log.i("debug", "index is $index")
        } else {
            Log.i("debug","Write city name")
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMyLocationEnabled(true)
        var url = ""
        lateinit var city : String

        val myLocation = TrackLocation(this).getCurrentLocation()
        if (myLocation != null) {
            val locationCoord = LatLng(myLocation.latitude, myLocation.longitude)

            val intent = intent
            val index = intent.getIntExtra("sight_clicked", -1)
            val city=intent.getStringExtra("city_name")



            if (city!=null){
                if (city.equals("Tallinn")){sightsInfo(resources.getStringArray(R.array.Tallinn))}
                else if (city.equals("Tartu")) {sightsInfo(resources.getStringArray(R.array.Tartu))}
                else if (city.equals("Narva")) {sightsInfo(resources.getStringArray(R.array.Narva))}
                else if (city.equals("Pärnu")) {sightsInfo(resources.getStringArray(R.array.Pärnu))}
                else if (city.equals("Viljandi")) {sightsInfo(resources.getStringArray(R.array.Viljandi))}
                else if (city.equals("Rakvere")) {sightsInfo(resources.getStringArray(R.array.Rakvere))}
                else if (city.equals("Võru")) {sightsInfo(resources.getStringArray(R.array.Võru))}
            } else {
                Log.i("debug","Write city name")
            }

            Log.i("debug", "$names")

            //Get the sight information about the chosen city


            //Adding markers for the chosen city
            for (i in 0 until names.size) {
                val marker = MarkerOptions().position(coord[i]).title(names[i])
                mMap.addMarker(marker)
            }

            var currentLoc = "${locationCoord.latitude},${locationCoord.longitude}"

            //Get the intent, which sight was chosen or whether the "go to all" was chosen
            /*val intent = intent
            val index = intent.getIntExtra("sight_clicked", -1)*/
            Log.i("MyDebug1", "${index.equals(1)}")
            if (index.equals(0)){
                if ((locationCoord.latitude - (coord[0].latitude)).absoluteValue < 0.03 &&
                    (locationCoord.longitude - (coord[0].longitude)).absoluteValue < 0.03) {
                    url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=$currentLoc&destination=${coord_string[0]}&mode=walking&key=AIzaSyDyno8oQ79071swJtQzGGOjIb-GcXi9GUA"
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord[0], 15F))
                }
            }
            else if (index.equals(1)) {
                if ((locationCoord.latitude - (coord[1].latitude)).absoluteValue < 0.03 &&
                    (locationCoord.longitude - (coord[1].longitude)).absoluteValue < 0.03
                ) {
                    url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=$currentLoc&destination=${coord_string[1]}&mode=walking&key=AIzaSyDyno8oQ79071swJtQzGGOjIb-GcXi9GUA"
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord[1], 15F))
                }
            }
            else if (index.equals(2)) {
                if ((locationCoord.latitude - (coord[2].latitude)).absoluteValue < 0.03 &&
                    (locationCoord.longitude - (coord[2].longitude)).absoluteValue < 0.03
                ) {
                    url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=$currentLoc&destination=${coord_string[2]}&mode=walking&key=AIzaSyDyno8oQ79071swJtQzGGOjIb-GcXi9GUA"
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord[2], 15F))
                }
            }
            else {
                //If the current location is too far from the sights, it shows walking route between the sights only
                if ((locationCoord.latitude - (coord[0].latitude + coord[1].latitude + coord[2].latitude) / 3).absoluteValue < 0.015 &&
                    (locationCoord.longitude - (coord[0].longitude + coord[1].longitude + coord[2].longitude) / 3).absoluteValue < 0.015
                ) {
                    url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=$currentLoc&destination=$currentLoc&mode=walking&waypoints=optimize:true|${coord_string[0]}|${coord_string[2]}|${coord_string[1]}&key=AIzaSyDyno8oQ79071swJtQzGGOjIb-GcXi9GUA"
                } else {
                    url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=${coord_string[0]}&destination=${coord_string[0]}&mode=walking&waypoints=optimize:true|${coord_string[2]}|${coord_string[1]}&key=AIzaSyDyno8oQ79071swJtQzGGOjIb-GcXi9GUA"
                }
            }
        if (url != ""){
        getDirectionsInfo(url)}
            Log.i("debug", "works")

            mMap.setOnMyLocationClickListener(this)

        }
    }


    fun getDirectionsInfo(url : String){
        val path: MutableList<List<LatLng>> = ArrayList()
        val options = PolylineOptions()
        options.color(Color.BLUE)
        options.width(10f)
        val allPoints : MutableList<LatLng> = ArrayList()
        allPoints.clear()

        val LatLongB = LatLngBounds.Builder()

        Ion.with(this)
            .load(url)
            .asJsonObject()
            .setCallback { error, result : JsonObject ->

                if (error != null) Log.i("MyDebug",error.toString())

                val routes = result.get("routes").asJsonArray
                val legs = routes.get(0).asJsonObject.get("legs").asJsonArray
                for (i in 0 until legs.size()) {
                    val dur = legs.get(i).asJsonObject.get("duration").asJsonObject
                    val minutes = dur.get("text").toString().split(" ")
                    duration += minutes[0].removeRange(0,1).toInt()
                    val steps = legs.get(i).asJsonObject.get("steps").asJsonArray


                    for (i in 0 until steps.size()) {
                        val points =
                            steps.get(i).asJsonObject.get("polyline").asJsonObject.get("points")
                                .asString

                        val polypts = points.flatMap { decodePoly(points) }

                        path.add(polypts)
                        for (point in polypts)  {
                            if (allPoints.contains(point)){
                            }
                            else{
                                options.add(point)
                                LatLongB.include(point)
                                allPoints.add(point)
                                Log.i("MyPoints", "$point")
                            }
                        }
                        allPoints.clear()
                    }
                    val bounds = LatLongB.build()
                    mMap.addPolyline(options)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                }
                val t = Toast.makeText(this, "$duration min", Toast.LENGTH_LONG)
                t.show()
                duration = 0
            }
    }

    private fun sightsInfo(info:Array<String>){
        names.clear()
        coord.clear()
        coord_string.clear()

        for (i in 0 until info.size) {
            var pieces = info[i].split(",")
            names.add(pieces[0])
            coord.add(LatLng(pieces[1].toDouble(), pieces[2].toDouble()))
            coord_string.add("${coord[i].latitude},${coord[i].longitude}")
        }
    }
    private fun decodePoly(encoded: String): List<LatLng> { //from https://github.com/irenenaya/Kotlin-Practice/blob/master/MapsRouteActivity.kt

        val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0

            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val p = LatLng(
                    lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5
                )
                poly.add(p)
            }
        return poly
    }


}


