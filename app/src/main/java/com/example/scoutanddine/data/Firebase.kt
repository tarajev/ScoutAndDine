package com.example.scoutanddine.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import com.example.scoutanddine.data.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.storage
import java.util.UUID

object FirebaseObject {

    fun addUser(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        profilePictureUri: Uri
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        uploadProfilePicture(profilePictureUri, { profilePictureURL ->
            Firebase.firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val user = User().apply {
                            this.email = email
                            this.username = username
                            this.name = fullName
                            this.phone = phoneNumber
                            this.image = profilePictureURL
                            this.id = auth.currentUser!!.uid
                        }

                        Firebase.firestore.collection("users").document(user.id).set(user)
                            .addOnSuccessListener {
                                Log.d("FirebaseHelper", "User data added successfully")
                                //successCallback()
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirebaseHelper", "Error adding document", e)
                                //failureCallback()
                            }
                    } else {
                        // Korisnik već postoji
                        Log.d("FirebaseHelper", "User already exists with email: $email")
                        //failureCallback()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseHelper", "Error checking user existence", e)
                    //failureCallback()
                }
        }, { exception ->
            // Greška prilikom čuvanja slike
            Log.w("FirebaseHelper", "Error uploading profile picture", exception)
            //failureCallback()
        })
    }
    private fun uploadProfilePicture(profilePictureUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = Firebase.storage.reference.child("profile_pictures/${UUID.randomUUID()}.jpg")
        storageRef.putFile(profilePictureUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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
