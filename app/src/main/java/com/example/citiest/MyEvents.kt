package com.example.citiest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_my_events.*

class MyEvents : AppCompatActivity(){
    val reqCode = 1
    val adapter = eventAdapter(this)
    val TAG = "weatherTAG"
    var idNum = 1

    lateinit var db: EventDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        db = Room.databaseBuilder(applicationContext, EventDatabase::class.java, "my_event_database")
            .allowMainThreadQueries()
            .build()

        myevents_listview.adapter = adapter

        load_current_events()

        val ev = adapter.gettEvents()
        ev.sortWith(compareBy({it[0].substring(6, 10).toInt()}, {it[0].substring(3, 5).toInt()}, {it[0].substring(0, 2).toInt()}))
        Log.i(TAG, ev.toString())

        add_event_button.setOnClickListener{
            //start add event intent/activity
            val new_event = Intent(this, AddEvent::class.java)
            startActivityForResult(new_event, reqCode)
        }

        myevents_listview.setOnItemClickListener {parent, view, position, id ->
            val item = parent.adapter.getItem(position) as MutableList<String>

            //don't delete yet
            //db.eventDao().deleteEvent(item[1])

            Log.i("storage", item.toString())
            val intent = Intent(this, AddEvent::class.java)
            intent.putExtra("date", item[0])
            intent.putExtra("title", item[1])
            intent.putExtra("loc", item[2])
            intent.putExtra("desc", item[3])
            intent.putExtra("id", item[4])
            intent.putExtra("edit", "edit")
            /*var i = 0
            var event = ""
            while (i < 4){
                event+=item.g
            }
            intent.putExtra("event", item)*/
            startActivityForResult(intent, reqCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == reqCode){
                val event = mutableListOf<String>()
                var string_event = ""
                if (data!=null){
                    //delete item in the list
                    if (data.hasExtra("delete")){
                        //remove existing from the database
                        val id = data.getStringExtra("id")
                        Log.i("storageTAG", "deleting id")
                        db.eventDao().deleteEvent(id)
                        //first, clear the adapter
                        adapter.clearEvents()
                        load_current_events()
                    } else {
                        //edit or add new item in the list
                        val list_title = data.getStringExtra("title")
                        val list_loc = data.getStringExtra("location")
                        val list_desc = data.getStringExtra("desc")
                        val list_date = data.getStringExtra("date")
                        if (list_date != null) {
                            event.add(list_date)
                            string_event += list_date
                        } else {
                            event.add("")
                            string_event += "no_date"
                        }
                        if (list_title != null) {
                            Log.i("weatherTAG", list_title)
                            event.add(list_title)
                            string_event += list_title
                        } else {
                            event.add("")
                            string_event += "no_title"
                        }
                        if (list_loc != null) {
                            event.add(list_loc)
                            string_event += list_loc
                        } else {
                            event.add("")
                            string_event += "no_location"
                        }
                        if (list_desc != null) {
                            event.add(list_desc)
                            string_event += list_desc
                        } else {
                            event.add("")
                            string_event += "no_description"
                        }
                        event.add("$idNum")
                        adapter.addEvent(event)
                        val new_event = Event(0, list_title, list_date, list_loc, list_desc)
                        idNum += 1
                        db.eventDao().insertEvent(new_event)

                        adapter.notifyDataSetChanged()
                        //editing an event
                        if (data.hasExtra("id")) {
                            //remove existing from the database
                            val id = data.getStringExtra("id")
                            Log.i("storageTAG", "deleting id")
                            db.eventDao().deleteEvent(id)
                            //first, clear the adapter
                            adapter.clearEvents()
                            load_current_events()
                        } else {
                            adapter.sortEvents()
                            adapter.notifyDataSetChanged()
                        }
                    }


                }
            }
        }
        /*//check if anything is inside the database
        val db_events = db.eventDao().loadAllEvents()
        val db_size = db_events.size
        var i = 0
        while (i < db_size){
            val db_event = db_events.get(i)
            Log.i("storage", db_event.title)
            i+=1
        }*/
    }

    fun load_current_events(){
        val db_events = db.eventDao().loadAllEvents()
        val db_size = db_events.size
        var i = 0
        Log.i("storageTAG", "db size = $db_size")
        while (i < db_size){
            val event = mutableListOf<String>()
            val db_event = db_events.get(i)
            if (db_event!=null)
            Log.i("storageTAG", db_event.id.toString())
            val date = db_event.date
            val title = db_event.title
            val loc = db_event.location
            val desc = db_event.description
            val id = db_event.id
            idNum = db_event.id+1
            if (date!=null && title!=null && loc!=null && desc!=null){
                event.add(date)
                event.add(title)
                event.add(loc)
                event.add(desc)
                event.add(id.toString())
            }
            i+=1
            adapter.addEvent(event)
        }
        adapter.sortEvents()
        adapter.notifyDataSetChanged()
    }

}
