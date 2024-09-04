package com.example.scoutanddine.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



class User {
    var id: String = ""
    var email: String = ""
    var username: String = ""
    var name: String = ""
    var phone: String = ""
    var image: String = ""
    var points: Int = 0
}

