package com.example.citiest

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.single_event_view.view.*

class eventAdapter(var context: Context): BaseAdapter(){

    val events = mutableListOf<List<String>>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        //build comparator


        //events.sort()

        //events.sortWith(compareBy({it[0].substring(6, 10).toInt()}, {it[0].substring(3, 5).toInt()}, {it[0].substring(0, 2).toInt()}))

        val view: View
        if (convertView == null){
            val layoutInflater = LayoutInflater.from(parent?.context)
            view = layoutInflater.inflate(R.layout.single_event_view, parent, false)
        } else{
            view = convertView
        }
        val event = getItem(position) as List<String>
        if (event.isNotEmpty()) {
            view.date_eventview.text = event[0]
            view.title_eventview.text = event[1]
            view.location_eventview.text = event[2]
            view.description_eventview.text = event[3]
        }

        return view

    }

    override fun getItem(position: Int): Any {
        return events[position]
    }

    override fun getItemId(position: Int): Long {
        return events[position].hashCode().toLong()
    }

    override fun getCount(): Int {
        return events.size
    }

    fun addEvent(event: List<String>){
        events.add(event)
    }

    fun clearEvents(){
        events.clear()
    }

    fun gettEvents(): MutableList<List<String>>{
        return events
    }

    fun sortEvents(){
        events.sortWith(compareBy({it[0].substring(6, 10).toInt()}, {it[0].substring(3, 5).toInt()}, {it[0].substring(0, 2).toInt()}))
    }

}