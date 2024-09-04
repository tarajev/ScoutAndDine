package com.example.scoutanddine.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.scoutanddine.R
import com.example.scoutanddine.data.entities.CafeRestaurant
import com.example.scoutanddine.data.FirebaseObject.addPoints
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    val visitedCafes = mutableListOf<String>()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val cafeRestaurants = mutableListOf<CafeRestaurant>() // Lista svih kafića/restorana
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        val locationchannel = NotificationChannel(
            "locationservicechannel",
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val objectChannel = NotificationChannel(
            "objectservicechannel",
            "CafeRestauran",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(locationchannel)
        notificationManager.createNotificationChannel(objectChannel)

        loadCafeRestaurants()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "locationservicechannel")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Log.d("SERVICE", location.toString())
                LocationInfo.location = location
                LocationInfo.alert()
                checkNearbyCafeRestaurants(location)
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        Log.d("LOCATION SERVICE", "Service started.")
        startForeground(1, notification.build())
    }

    private fun stop() {
        Log.d("LOCATION SERVICE", "Service stopped.")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun stopService(name: Intent?): Boolean {
        stop()
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }


    private fun checkNearbyCafeRestaurants(location: Location) {
        for (cafeRestaurant in cafeRestaurants) {
            val cafeLocation = Location("").apply {
                latitude = cafeRestaurant.location.latitude
                longitude = cafeRestaurant.location.longitude
            }

            if (location.distanceTo(cafeLocation) < 200 && !visitedCafes.contains(cafeRestaurant.id)) {
                sendNotification(cafeRestaurant)
                Log.d("NOTIFIKACIJA", "POSLATA")

                // Dodavanje kafića i postavljanje tajmera - brisanje posle 3h
                visitedCafes.add(cafeRestaurant.id)
                scheduleCafeRemoval(cafeRestaurant.id,  3 * 60 * 60 *1000L) // 3 sata
            }
        }
    }

    private fun loadCafeRestaurants() {
        db.collection("objects").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cafeRestaurant = document.toObject(CafeRestaurant::class.java)
                    cafeRestaurants.add(cafeRestaurant)
                }
                Log.d("SERVICE", "Učitano ${cafeRestaurants.size} objekata.")
            }
            .addOnFailureListener { e ->
                Log.e("SERVICE", "Greška prilikom učitavanja objekata: ${e.message}")
            }
    }

    private fun scheduleCafeRemoval(cafeId: String, delay: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delay)
            visitedCafes.remove(cafeId)
            Log.d("NOTIFIKACIJA", "Uklonjen kafić: $cafeId iz liste poseta.")
        }
    }

    private fun sendNotification(cafeRestaurant: CafeRestaurant) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("cafeId", cafeRestaurant.id)
            action = "hereAction|${cafeRestaurant.id}"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            cafeRestaurant.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val hereAction = NotificationCompat.Action(
            R.drawable.outline_check_circle_outline_24,
            "I am here!",
            pendingIntent
        )

        val notification = NotificationCompat.Builder(this, "objectservicechannel")
            .setContentTitle("Nearby ${cafeRestaurant.name}")
            .setContentText("${cafeRestaurant.name} is within 200 meters of your location.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .addAction(hereAction) // Dodaj akciju u notifikaciju
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(cafeRestaurant.id.hashCode(), notification)
    }
}

class NotificationReceiver : BroadcastReceiver() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onReceive(context: Context, intent: Intent) {
        val cafeId = intent.getStringExtra("cafeId") ?: return
 Log.d("KORISNIK", "nesto kao")
        // funkcija za dodavanje poena
        addPoints(auth.currentUser!!.uid, 1)
        // sklanjamo notifikaciju
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(cafeId.hashCode())
    }
}
