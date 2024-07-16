package com.example.scoutanddine.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import com.example.scoutanddine.data.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.GeoPoint

object FirebaseObject {

    fun addUser(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        profilePictureURL: String,
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Check if user already exists
        Firebase.firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // User doesn't exist, create new user
                    val user = User()
                        user.email = email
                        user.username = username
                        user.name = fullName
                        user.phone = phoneNumber
                        user.image = profilePictureURL
                        user.id = auth.currentUser!!.uid

                    Firebase.firestore.collection("users").document(user.id).set(user).addOnSuccessListener {
                        Log.d("FirebaseHelper", "User data added successfully")
                        //successCallback()
                    }.addOnFailureListener { e ->
                        Log.w("FirebaseHelper", "Error adding document", e)
                    }
                } else {
                    // User already exists
                    Log.d("FirebaseHelper", "User already exists with email: $email")
                   // failureCallback()
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseHelper", "Error checking user existence", e)
                //failureCallback()
            }
    }

    fun addCafeRestaurant(
        name: String,
        latitude: Double,
        longitude: Double,
        rating: Double,
        comments: List<String>,
        type: String,
        successCallback: () -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        val location = GeoPoint(latitude, longitude)
        val cafeRestaurant = CafeRestaurant(
            name = name,
            location = location,
            rating = rating,
            comments = comments,
            type = type
        )

        Firebase.firestore.collection("objects")
            .add(cafeRestaurant)
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "CafeRestaurant added successfully with ID: ${it.id}")
                successCallback()
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseHelper", "Error adding CafeRestaurant", e)
                failureCallback(e)
            }
    }

    }
