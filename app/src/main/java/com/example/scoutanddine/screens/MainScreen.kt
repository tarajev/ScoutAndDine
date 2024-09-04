import android.location.Location
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.scoutanddine.components.FilterDialog
import com.example.scoutanddine.components.SearchBar
import com.example.scoutanddine.data.entities.CafeRestaurant
import com.example.scoutanddine.data.FirebaseObject
import com.example.scoutanddine.screens.AddObjectDialog
import com.example.scoutanddine.services.LocationInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.scoutanddine.extras.calculateDistance
import kotlin.math.round

@Composable
fun MainScreen(navController: NavHostController) {
    var userLocation by remember { mutableStateOf<Location?>(LocationInfo.location) }
    val context = LocalContext.current
    var showAddCafeDialog by remember { mutableStateOf(false) }
    var cafeRestaurants by remember { mutableStateOf<List<CafeRestaurant>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(("")) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isFilterDialogVisible by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf("All") }
    var selectedRating by remember { mutableStateOf(0) }
    var onlyAvailable by remember { mutableStateOf(false) }
    var searchRadius by remember { mutableStateOf(0f) }
    var filteredCafeRestaurants by remember { mutableStateOf<List<CafeRestaurant>>(emptyList()) }

    fun subscriber(location: Location?) {
        userLocation = location
        Log.e("HOME", "Home: $location")
    }

    LocationInfo.subscribe(::subscriber)

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
                filteredCafeRestaurants = cafes
            },
            onFailure = { exception ->
                Log.w("MainScreen", "Error fetching cafes and restaurants", exception)
            }
        )
    }

    fun applyFilters() {
        filteredCafeRestaurants = cafeRestaurants.filter { cafe ->
            (selectedTag == "All" || cafe.type.contains(selectedTag, true)) &&
                    cafe.rating >= selectedRating &&
                    (!onlyAvailable || cafe.crowdInfo != "Nema slobodnih mesta") &&
                    (searchQuery.isEmpty() || cafe.name.contains(searchQuery, true)) &&
                    (searchRadius == 0f || userLocation == null || calculateDistance(
                        cafe.location.latitude,
                        cafe.location.longitude,
                        userLocation!!
                    ) <= searchRadius)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
            .background(Color.Transparent)
    ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    isSearchVisible = false
                }
            ) {
                userLocation?.let {
                    Marker(
                        state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                        title = "You are here",
                        snippet = "Da li želite da dodate objekat?",
                        onInfoWindowClick = { showAddCafeDialog = true }
                    )
                }
                filteredCafeRestaurants.forEach { cafe ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                cafe.location.latitude,
                                cafe.location.longitude
                            )
                        ),
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Transparent)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSearchVisible) {
                    IconButton(
                        onClick = { isSearchVisible = !isSearchVisible },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isSearchVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { applyFilters() }
                    )

                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = { isFilterDialogVisible = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, shape = CircleShape)
                    .clip(CircleShape)
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter")
            }
        }
            if (showAddCafeDialog) {
                userLocation?.let {
                    AddObjectDialog(
                        onDismiss = { showAddCafeDialog = false },
                        onSubmit = { cafeRestaurant ->
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
                                    cafeRestaurants = cafeRestaurants + cafeRestaurant
                                    filteredCafeRestaurants = filteredCafeRestaurants + cafeRestaurant
                                },
                                failureCallback = { e ->
                                    Log.w("MainScreen", "Error adding CafeRestaurant", e)
                                }
                            )
                        },
                        lng = it.longitude,
                        lat = it.latitude
                    )
                }
            }
        }
    }
    if (isFilterDialogVisible) {
        FilterDialog(
            selectedTag = selectedTag,
            onTagSelected = { selectedTag = it },
            selectedRating = selectedRating,
            onRatingChanged = { selectedRating = it },
            onlyAvailable = onlyAvailable,
            onAvailableChanged = { onlyAvailable = it },
            searchRadius = searchRadius,
            onRadiusChanged = { searchRadius = round(it * 2) / 2 },
            onApplyFilters = {
                applyFilters()
                isFilterDialogVisible = false
            },
            onDismiss = { isFilterDialogVisible = false }
        )
    }
}
