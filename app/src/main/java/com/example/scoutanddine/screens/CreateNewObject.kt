package com.example.scoutanddine.screens

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.firestore.GeoPoint
import com.example.scoutanddine.data.CafeRestaurant
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Locale


@Composable
fun AddObjectDialog(onDismiss: () -> Unit, onSubmit: (CafeRestaurant) -> Unit, lat: Double, lng: Double) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var priceTo by remember { mutableStateOf("") }
    var priceFrom by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf("") }
    val latitude by remember { mutableStateOf(lat) }
    val longitude by remember { mutableStateOf(lng) }
    val context = LocalContext.current



    LaunchedEffect(latitude, longitude) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                address = addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error getting address: ${e.localizedMessage}")
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Dodajte novi objekat")},
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") }
                )
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = priceFrom,
                        onValueChange = { priceFrom = it },
                        label = { Text("Price From") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Text(" ━ ")
                    TextField(
                        value = priceTo,
                        onValueChange = { priceTo = it },
                        label = { Text("Price To") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                TextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Working Hours") }
                )
                /*TextField(
                    value = reviews,
                    onValueChange = { reviews = it },
                    label = { Text("Reviews") }
                )
                */

            }
        },
        confirmButton = {
            Button(onClick = {
                val location = GeoPoint(latitude, longitude)

                onSubmit(
                    CafeRestaurant(
                        name = name,
                        location = location,
                        address = address,
                        rating = 0.0,
                        type = type,
                        priceFrom = priceFrom.toInt(),
                        priceTo = priceTo.toInt(),
                        hours = hours
                    )
                )
                onDismiss()
            }) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Otkaži")
            }
        }
    )
}
