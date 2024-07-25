package com.example.scoutanddine.data

import com.google.firebase.firestore.GeoPoint

class CafeRestaurant(
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    var address: String = "",
    var rating: Double = 0.0,
    var comments: List<String> = listOf(),
    var type: String = "",
    var priceTo: Int = 0,
    var priceFrom: Int = 0,
    var hours: String = ""
)