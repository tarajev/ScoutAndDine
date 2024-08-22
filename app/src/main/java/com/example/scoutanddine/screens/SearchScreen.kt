package com.example.scoutanddine.screens


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.scoutanddine.data.CafeRestaurant
import com.example.scoutanddine.components.SearchBar
import com.example.scoutanddine.data.FirebaseObject.searchForCafeRestaurants
import com.example.scoutanddine.services.LocationInfo
import kotlin.math.round

@Composable
fun SearchScreen(
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf(("")) }
    var selectedTag by remember { mutableStateOf("All") }
    var selectedRating by remember { mutableStateOf(0) }
    var onlyAvailable by remember { mutableStateOf(false) }
    var searchRadius by remember { mutableStateOf(0f) }
    var searchResults by remember { mutableStateOf<List<CafeRestaurant>>(emptyList()) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current

    fun subscriber(location: Location?) {
        userLocation = location
        Log.e("HOME", "Home: ${location?.latitude.toString()}, ${userLocation?.longitude}")
    }


   LocationInfo.subscribe(::subscriber)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tags (Kafić, Restoran)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedTag == "All",
                    onClick = { selectedTag = "All" },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedTag == "Kafic",
                    onClick = { selectedTag = "Kafic" },
                    label = { Text("Kafić") }
                )
                FilterChip(
                    selected = selectedTag == "Restoran",
                    onClick = { selectedTag = "Restoran" },
                    label = { Text("Restoran") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Filter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Min. Rating:")
                Spacer(modifier = Modifier.width(8.dp))
                RatingBar(rating = selectedRating) { newRating ->
                    selectedRating = newRating
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Only Available Places Filter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = onlyAvailable,
                    onCheckedChange = { onlyAvailable = it }
                )
                Text("Samo dostupni objekti")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Radius Search
            Text("Pretraga u radijusu (u km): $searchRadius")
            Slider(
                value = searchRadius,
                onValueChange = { searchRadius = round(it * 2) / 2 },
                valueRange = 0f..50f,
                steps = 100
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Button
            Button(
                onClick = {
                    searchForCafeRestaurants(
                        searchQuery,
                        selectedTag,
                        selectedRating,
                        onlyAvailable,
                        searchRadius,
                        userLocation
                    ) { results ->
                        searchResults = results
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pretraži")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(searchResults) { cafeRestaurant ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate("details/${cafeRestaurant.id}") },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(text = cafeRestaurant.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ocena: ${cafeRestaurant.rating}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(text = "Tip: ${cafeRestaurant.type}", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        text = "Adresa: ${cafeRestaurant.address}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Cene: ${cafeRestaurant.priceFrom} - ${cafeRestaurant.priceTo} RSD",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun RatingBar(rating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChange(i) }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarOutline,
                    contentDescription = null,
                    tint = if (i <= rating) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
