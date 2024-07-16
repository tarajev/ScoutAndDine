package com.example.scoutanddine.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "image") val image: String
)


class User {
    var id: String = ""
    var email: String = ""
    var username: String = ""
    var name: String = ""
    var phone: String = ""
    var image: String = ""
}

