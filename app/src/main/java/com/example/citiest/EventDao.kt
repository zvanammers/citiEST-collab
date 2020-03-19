package com.example.citiest

import androidx.room.*

@Dao
interface EventDao {

    @Query("SELECT * FROM event")
    fun loadAllEvents(): Array<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(vararg events: Event)

    //@Delete
    //fun deleteEvent (vararg events: Event)

    @Query("DELETE FROM event WHERE id = :arg0")
    fun deleteEvent(arg0: String)

    @Query("DELETE FROM event")
    fun deleteAllEvents()
}