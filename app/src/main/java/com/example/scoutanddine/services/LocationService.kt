package com.example.scoutanddine.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.scoutanddine.MainActivity
import com.example.scoutanddine.R
import com.example.scoutanddine.data.CafeRestaurant
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LocationService : Service() {
    companion object {
        const val CHANNEL_ID = "location_service_channel"
        const val EXTRA_LOCATION = "location"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()
        startForeground(1, createNotification())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    sendLocationUpdate(location)
                    checkNearbyCafeRestaurants(location)
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(5000)
        }.build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun sendLocationUpdate(location: Location) {
        val intent = Intent("com.example.LOCATION_UPDATE").apply {
            putExtra(EXTRA_LOCATION, location)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun checkNearbyCafeRestaurants(location: Location) {
        db.collection("objects").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cafeRestaurant = document.toObject(CafeRestaurant::class.java)
                    val cafeLocation = Location("").apply {
                        latitude = cafeRestaurant.location.latitude
                        longitude = cafeRestaurant.location.longitude
                    }
                    if (location.distanceTo(cafeLocation) < 10) { // Check if within 100 meters
                        sendNotification(cafeRestaurant)
                    }
                }
            }
    }

    private fun sendNotification(cafeRestaurant: CafeRestaurant) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nearby Cafe/Restaurant")
            .setContentText("You are near ${cafeRestaurant.name}")
            //.setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(cafeRestaurant.name.hashCode(), notification)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Tracking your location")
            //.setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}