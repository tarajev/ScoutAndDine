package com.example.scoutanddine.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.scoutanddine.R
import com.example.scoutanddine.components.ReviewContent
import com.example.scoutanddine.data.entities.CafeRestaurant
import com.example.scoutanddine.data.FirebaseObject
import com.example.scoutanddine.data.FirebaseObject.fetchCafeRestaurantById
import com.example.scoutanddine.data.FirebaseObject.uploadImageRestaurant
import java.io.File
import java.util.UUID


@Composable
fun ObjectDetailsScreen(navController: NavController, cafeRestaurantID: String) {
    var cafeRestaurant by remember { mutableStateOf<CafeRestaurant?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCrowdDialog by remember { mutableStateOf<Boolean>(false) }
    var showReviewDialog by remember { mutableStateOf<Boolean>(false) }
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var crowdOption1 by remember { mutableStateOf(false) }
    var crowdOption2 by remember { mutableStateOf(false) }
    var crowdOption3 by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fetchCafeRestaurantById(
        id = cafeRestaurantID,
        onSuccess = { cafe ->
            cafeRestaurant = cafe
            isLoading = false
        },
        onFailure = { e ->
            errorMessage = e.message
            isLoading = false
        }
    )

    fun createImageFile(context: Context): Uri {
        val storageDir: File? = context.getExternalFilesDir(null)
        val file = File.createTempFile(
            "JPEG_${UUID.randomUUID()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri?.let { uri ->
                    imageUrl = uri.toString() // Store image URL
                }
            }
        }

    val requestCameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val photoUri = createImageFile(context)
                selectedImageUri = photoUri
                takePictureLauncher.launch(photoUri)
            } else {
                // Handle permission denied case
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Images
        if (cafeRestaurant?.imageUrls?.isNotEmpty() == true) {
            LazyRow {
                items(cafeRestaurant!!.imageUrls) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cafe name and average rating
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Text(
                text = cafeRestaurant?.name ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = cafeRestaurant?.rating.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Zvezdica",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Address
        Text(
            text = cafeRestaurant?.address ?: "",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Price range
        Text(
            text = "${cafeRestaurant?.priceFrom}rsd - ${cafeRestaurant?.priceTo}rsd",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Crowd
        Text(
            text = if (!cafeRestaurant?.crowdInfo.isNullOrEmpty()) {
                "*${cafeRestaurant?.crowdInfo} : ${cafeRestaurant?.lastUpdated}"
            } else {
                "Nema informacija o gužvi"
            },
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews
        if (cafeRestaurant?.reviews?.isNotEmpty() == true) {
            Text(
                text = "Recenzije:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cafeRestaurant!!.reviews) { review ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(horizontal = 8.dp)
                            .shadow(
                                4.dp,
                                spotColor = Color.Black,
                                ambientColor = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { isExpanded = !isExpanded }, // Klik za proširenje
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        if (isExpanded) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .verticalScroll(rememberScrollState()) // Omogućava skrolovanje
                            ) {
                                ReviewContent(review)
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                            ) {
                                ReviewContent(review, maxLines = 1) // prikazuje se samo jedan red
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(51, 204, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 8.dp),
                onClick = { showReviewDialog = true }
            ) {
                Text(text = "Ostavi recenziju", color = Color.White)
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(51, 204, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp),
                onClick = { showCrowdDialog = true }
            ) {
                Text(text = "Prijavi gužvu", color = Color.White)
            }
        }

        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = { Text(text = "Ostavi recenziju") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = {
                                if (it.length <= 200) {
                                    reviewText = it
                                }
                            },
                            label = { Text("Recenzija") }

                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Ocena:")
                        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = { rating = i },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (i <= rating) Color.Black else Color.Gray
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    51,
                                    204,
                                    255
                                )
                            ),
                            onClick = {
                                val permissionCheckResult = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    val photoUri = createImageFile(context)
                                    selectedImageUri = photoUri
                                    takePictureLauncher.launch(photoUri)
                                } else {
                                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        ) {
                            Text("Dodaj sliku")
                        }
                        selectedImageUri?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                },

                confirmButton = {
                    Button(colors = ButtonDefaults.buttonColors(
                        containerColor = Color(
                            51,
                            204,
                            255
                        )

                    ),
                        onClick = {
                            if (selectedImageUri != null) {
                                uploadImageRestaurant(
                                    cafeRestaurantID = cafeRestaurantID,
                                    imageUri = selectedImageUri!!,
                                    onSuccess = {
                                        FirebaseObject.addReview(
                                            cafeRestaurantID,
                                            reviewText,
                                            rating
                                        )
                                        showReviewDialog = false
                                    },
                                    onFailure = {
                                        // Handle failure
                                    }
                                )
                            } else {
                                FirebaseObject.addReview(cafeRestaurantID, reviewText, rating)
                                showReviewDialog = false
                            }
                        }) {
                        Text("Potvrdi")
                    }
                },
                dismissButton = {
                    Button(colors = ButtonDefaults.buttonColors(
                        containerColor = Color(
                            51,
                            204,
                            255
                        )
                    ),
                        onClick = {
                            showReviewDialog = false
                        }) {
                        Text("Otkaži")
                    }
                }
            )
        }

        if (showCrowdDialog) {
            AlertDialog(
                onDismissRequest = { showCrowdDialog = false },
                title = { Text(text = "Prijavi gužvu") },
                text = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = crowdOption1,
                                onCheckedChange = { crowdOption1 = it }
                            )
                            Text(text = "Nema slobodnih mesta")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = crowdOption2,
                                onCheckedChange = { crowdOption2 = it }
                            )
                            Text(text = "Ima nekoliko slobodnih mesta")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = crowdOption3,
                                onCheckedChange = { crowdOption3 = it }
                            )
                            Text(text = "Mnogo slobodnih mesta")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(51, 204, 255)),
                        onClick = {
                            var info = ""
                            if (crowdOption1)
                                info = "Nema slobodnih mesta"
                            else if (crowdOption2)
                                info = "Ima nekoliko slobodnih mesta"
                            else
                                info = "Mnogo slobodnih mesta"

                            FirebaseObject.addCrowdednessInformation(cafeRestaurantID, info)
                            showCrowdDialog = false
                        }) {
                        Text("Potvrdi")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showCrowdDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(51, 204, 255)),
                    ) {
                        Text("Otkaži")
                    }
                }
            )
        }

    }
}
