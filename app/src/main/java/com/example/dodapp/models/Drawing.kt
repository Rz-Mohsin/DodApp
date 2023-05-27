package com.example.dodapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dodapp.db.MarkerConverter
import java.io.Serializable

@Entity(
    tableName = "drawings"
)
data class Drawing(
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    val name : String,
    val creationTime : Long,
    val imageBytes : ByteArray,
    @TypeConverters(MarkerConverter::class)
    var markers : MutableList<Marker>
) : Serializable
