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
import com.example.scoutanddine.ui.theme.ScoutAndDineTheme
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.scoutanddine.navigation.NavBar
import com.example.scoutanddine.services.LocationService


class MainActivity : ComponentActivity() {
    var i: Intent? = null
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isServiceEnabled = sharedPreferences.getBoolean("service_enabled", true)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBGLocationPermission()
        }

        val serviceIntent = Intent(this, LocationService::class.java)
        if(isServiceEnabled)
        startService(serviceIntent)

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
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBGLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            0
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScoutAndDineTheme {
        NavBar()
    }
}