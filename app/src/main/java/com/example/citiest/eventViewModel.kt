package com.example.citiest

import androidx.lifecycle.ViewModel

class eventViewModel: ViewModel() {

    //data class event(var date: String, var title: String, var loc: String, var desc: String)
    val eventList = mutableListOf<List<String>>()

    fun getEvents(): MutableList<List<String>>{
        return eventList
    }

    fun addEvent(event: List<String>){
        eventList.add(event)
    }

}