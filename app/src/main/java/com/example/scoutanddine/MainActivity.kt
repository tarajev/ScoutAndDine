package com.example.scoutanddine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scoutanddine.ui.theme.ScoutAndDineTheme
import com.example.scoutanddine.navigation.NavBar
import com.example.scoutanddine.services.LocationService


class MainActivity : ComponentActivity() {
    var i: Intent? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isServiceEnabled = sharedPreferences.getBoolean("service_enabled", true)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ako dozvole nisu dodeljene, trazimo ih
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
                val serviceIntent = Intent(this, LocationService::class.java)
                startService(serviceIntent)

        } else {
            if (isServiceEnabled) {
                val serviceIntent = Intent(this, LocationService::class.java)
                startService(serviceIntent)
            }
        }
        setContent {
            ScoutAndDineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavBar()
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(i)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ako je dozvola odobrena, pokreni servis
                val serviceIntent = Intent(this, LocationService::class.java)
                startService(serviceIntent)
            } else {
                // Ako je dozvola odbijena, možeš prikazati poruku ili logiku za odbijanje
                Toast.makeText(this, "Location permission is required to run the service", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScoutAndDineTheme {
        NavBar()
    }
}*/