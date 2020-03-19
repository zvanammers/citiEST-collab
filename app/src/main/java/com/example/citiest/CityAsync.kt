package com.example.citiest

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class CityAsync(activity: MainActivity): AsyncTask<String, Int, String>() {

    val TAG = "weatherTAG"

    var delegate: AsyncResponse = activity

    override fun doInBackground(vararg accessURL: String?): String? {

        var data = ""
        val iStream: InputStream
        val urlConnection: HttpURLConnection
        try {
            val url = URL(accessURL[0])
            urlConnection = url.openConnection() as HttpURLConnection
            /*urlConnection.requestMethod = ("GET")
            urlConnection.doInput = (true)
            urlConnection.doOutput = (true)*/
            urlConnection.connect()
            val status = urlConnection.responseCode
            val error = urlConnection.errorStream
            Log.i(TAG, "status=$status, error=$error")
            iStream = urlConnection.inputStream

            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            br.forEachLine { line ->
                sb.append(line)
            }
            data = sb.toString()
            br.close()

            //val http = MapHttpConnection()
            //val data = http.readURL(url)
            //val response = getJSONObjectFromURL(url)
            //return data

            iStream.close()
            urlConnection.disconnect()

        } catch (e: Exception){
            Log.i(TAG, "error is: ${e.printStackTrace()}")
        } finally{
        }
        return data
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        //Log.i(TAG, result)
        delegate.processFinish(result)
    }

}