package com.example.scoutanddine.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import com.example.scoutanddine.data.entities.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object FirebaseObject {
    private val auth = FirebaseAuth.getInstance()
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

    private fun uploadProfilePicture(
        profilePictureUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef =
            Firebase.storage.reference.child("profile_pictures/${UUID.randomUUID()}.jpg")
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
        address: String,
        rating: Double,
        type: String,
        priceTo: Int,
        priceFrom: Int,
        hours: String,
        successCallback: () -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        val location = GeoPoint(latitude, longitude)
        val cafeRestaurant = CafeRestaurant(
            name = name,
            location = location,
            address = address,
            rating = rating,
            type = type,
            hours = hours,
            priceTo = priceTo,
            priceFrom = priceFrom
        )

        val newDocument = Firebase.firestore.collection("objects").document()
        cafeRestaurant.id = newDocument.id
        //Firebase.firestore.collection("objects").add(cafeRestaurant)
        Firebase.firestore.collection("objects").document(newDocument.id).set(cafeRestaurant)
            .addOnSuccessListener {
                Log.d(
                    "FirebaseHelper",
                    "CafeRestaurant added successfully with ID: ${cafeRestaurant.id}"
                )
                successCallback()
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseHelper", "Error adding CafeRestaurant", e)
                failureCallback(e)
            }
    }

    fun fetchCafesRestaurants(
        onSuccess: (List<CafeRestaurant>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Firebase.firestore.collection("objects")
            .get()
            .addOnSuccessListener { result ->
                val cafeRestaurants = result.mapNotNull { document ->
                    val cafeRestaurant = document.toObject<CafeRestaurant>()
                    cafeRestaurant.id = document.id
                    cafeRestaurant
                }
                onSuccess(cafeRestaurants)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    /* fun fetchCafeRestaurantById(id: String, onSuccess: (CafeRestaurant) -> Unit, onFailure: (Exception) -> Unit) {

         Firebase.firestore.collection("objects").document(id).get()
             .addOnSuccessListener { document ->
                 if (document.exists()) {
                     val cafeRestaurant = document.toObject(CafeRestaurant::class.java)
                     cafeRestaurant?.id = document.id
                     onSuccess(cafeRestaurant!!)
                 } else {
                     onFailure(Exception("Document does not exist"))
                 }
             }
             .addOnFailureListener { e ->
                 onFailure(e)
             }
     }*/

    fun fetchCafeRestaurantById(
        id: String,
        onSuccess: (CafeRestaurant) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Reference to the CafeRestaurant document
        val cafeDocumentRef = Firebase.firestore.collection("objects").document(id)

        // First, get the CafeRestaurant document
        cafeDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Deserialize the CafeRestaurant document
                    val cafeRestaurant = document.toObject(CafeRestaurant::class.java)
                    cafeRestaurant?.id = document.id

                    // Now, get the reviews from the subcollection
                    cafeDocumentRef.collection("reviews").get()
                        .addOnSuccessListener { reviewDocuments ->
                            val reviews = reviewDocuments.mapNotNull { reviewDoc ->
                                reviewDoc.toObject(Review::class.java)
                            }
                            // Set the reviews to the cafeRestaurant object
                            cafeRestaurant?.reviews = reviews
                            onSuccess(cafeRestaurant!!)
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    onFailure(Exception("Document does not exist"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addCrowdednessInformation(id: String, crowdInfo: String) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val current = LocalDateTime.now().format(formatter).toString()
        val updates = hashMapOf<String, Any>(
            "crowdInfo" to crowdInfo,
            "lastUpdated" to current
        )
        Firebase.firestore.collection("objects").document(id).update(updates)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Document successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreUpdate", "Error updating document", e)
            }
    }

    fun addReview(cafeId: String, reviewText: String, rating: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val current = LocalDateTime.now().format(formatter).toString()

        Firebase.firestore.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)

                    val review = Review(
                        reviewText = reviewText,
                        rating = rating,
                        user = user!!.username,
                        date = current
                    )
                    val reviewId = Firebase.firestore.collection("objects").document(cafeId)
                        .collection("reviews").document().id

                    Firebase.firestore.collection("objects").document(cafeId)
                        .collection("reviews").document(reviewId)
                        .set(review)
                        .addOnSuccessListener {
                            Log.d("FirestoreUpdate", "Review successfully added!")
                            //updateAverageRating(cafeId) // azuriranje prosecne ocene implementirati
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreUpdate", "Error adding review", e)
                        }
                } else {
                    Log.d("Firebase", "Document doesn't exist")
                }
            }.addOnFailureListener { e ->
                Log.w("Firebase", "Error getting user", e)
            }
    }
}
