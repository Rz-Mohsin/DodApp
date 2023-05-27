package com.example.dodapp.models

data class Marker(
    val markerID : String,
    val positionX : Float,
    val positionY : Float,
    val title : String,
    val description : String,
    val creationTime : Long
)
