package com.example.dodapp.db

import androidx.room.TypeConverter
import com.example.dodapp.models.Marker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MarkerConverter {

    @TypeConverter
    fun fromMarkers(markers : MutableList<Marker>) : String {
        return Gson().toJson(markers)
    }

    @TypeConverter
    fun toMarkers(json : String) : MutableList<Marker> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<Marker>>() {}.type
        return gson.fromJson(json, type)
    }
}