package com.example.scoutanddine.data.entities

import com.google.firebase.firestore.GeoPoint

class CafeRestaurant(
    var id: String = "",
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    var address: String = "",
    var rating: Double = 0.0,
    var reviewCount: Int = 0,
    var ratingSum: Int = 0,
    var reviews: List<Review> = listOf(),  //podkolekcije
    var type: String = "",
    var priceTo: Int = 0,
    var priceFrom: Int = 0,
    var hours: String = "",
    var imageUrls: List<String> = listOf(),
    var crowdInfo:String = "",
    var lastUpdated: String = ""
)