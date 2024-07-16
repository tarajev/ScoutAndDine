package com.example.scoutanddine.data

import com.google.firebase.firestore.GeoPoint

class CafeRestaurant(
    val name: String = "",
    val location: GeoPoint,
    val rating: Double = 0.0,
    val comments: List<String> = listOf(),
    val type: String = ""
)