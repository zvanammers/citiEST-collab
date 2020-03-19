package com.example.citiest

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Event::class), version=1)
abstract class EventDatabase: RoomDatabase(){
    abstract fun eventDao(): EventDao
}