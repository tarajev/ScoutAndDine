import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import com.example.scoutanddine.data.CafeRestaurant
import com.example.scoutanddine.data.FirebaseObject
import com.example.scoutanddine.screens.AddObjectDialog
import com.example.scoutanddine.services.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MainScreen(navController: NavHostController) {
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    var showAddCafeDialog by remember { mutableStateOf(false) }
    var cafeRestaurants by remember { mutableStateOf<List<CafeRestaurant>>(emptyList()) }

    fun subscriber(location: Location?) {
        userLocation = location
        Log.e("HOME", "Home: ${location?.latitude.toString()}, ${userLocation?.longitude}")
    }


    DisposableEffect(Unit) {
        val intentFilter = IntentFilter("com.example.LOCATION_UPDATE")
        val receiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: Context, intent: Intent) {
                val location = intent.getParcelableExtra(LocationService.EXTRA_LOCATION, Location::class.java)
                Log.d("MainScreen", "Broadcast received with location: ${location?.latitude}, ${location?.longitude}")
                subscriber(location)
            }
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter)

        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 20f)
    }

   LaunchedEffect(userLocation) {
        userLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            val zoomLevel = cameraPositionState.position.zoom
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, zoomLevel)
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }
    }

    LaunchedEffect(Unit) {
        FirebaseObject.fetchCafesRestaurants(
            onSuccess = { cafes ->
                cafeRestaurants = cafes
            },
            onFailure = { exception ->
                Log.w("MainScreen", "Error fetching cafes and restaurants", exception)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    title = "You are here",
                    snippet = "Da li želite da dodate objekat?",
                    onInfoWindowClick = { showAddCafeDialog = true }
                )
            }
            cafeRestaurants.forEach { cafe ->
                Marker(
                    state = MarkerState(position = LatLng(cafe.location.latitude, cafe.location.longitude)),
                    title = cafe.name,
                    snippet = cafe.address,
                   /* onClick =  {
                        val cafeID = cafe.id
                        navController.navigate("details/$cafeID")
                        true
                    },*/
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    onInfoWindowLongClick = {
                        Log.d("OVO JE ID", cafe.id)
                        val cafeID = cafe.id
                        navController.navigate("details/$cafeID")
                    }
                )
            }
        }

        if (showAddCafeDialog) {
            userLocation?.let {
                AddObjectDialog(onDismiss = { showAddCafeDialog = false }, onSubmit = { cafeRestaurant ->
                    // Save cafeRestaurant to Firestore
                    FirebaseObject.addCafeRestaurant(
                        name = cafeRestaurant.name,
                        latitude = cafeRestaurant.location.latitude,
                        longitude = cafeRestaurant.location.longitude,
                        address = cafeRestaurant.address,
                        rating = 0.0,
                        type = cafeRestaurant.type,
                        priceFrom = cafeRestaurant.priceFrom,
                        priceTo = cafeRestaurant.priceTo,
                        hours = cafeRestaurant.hours,
                        successCallback = {
                            Log.d("MainScreen", "CafeRestaurant added successfully")
                        },
                        failureCallback = { e ->
                            Log.w("MainScreen", "Error adding CafeRestaurant", e)
                        }
                    )
                }, lng = it.longitude, lat = it.latitude)
            }
        }
    }
}
