package com.example.scoutanddine.data.entities

import com.google.firebase.firestore.GeoPoint

class User(
    var id: String = "",
    var email: String = "",
    var username: String = "",
    var name: String = "",
    var phone: String = "",
    var image: String = "",
    var points: Int = 0
)