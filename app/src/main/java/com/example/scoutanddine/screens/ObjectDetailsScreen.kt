package com.example.scoutanddine.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.scoutanddine.R
import com.example.scoutanddine.data.CafeRestaurant
import com.example.scoutanddine.data.FirebaseObject
import com.example.scoutanddine.data.FirebaseObject.fetchCafeRestaurantById


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

        // Cafe name
        Text(
            text = cafeRestaurant?.name ?: "",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Start)
        )

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
            text = "*${cafeRestaurant?.crowdInfo} : ${cafeRestaurant?.lastUpdated}",
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

            cafeRestaurant!!.reviews.forEach { review ->
                Text(
                    text = "Ocena: ${review.rating} zvezdica",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = review.reviewText,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Autor: ${review.user}, Datum: ${review.date}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            }

            Row() {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    onClick = { showReviewDialog = true }

                ) {
                    Text(text = "Ostavi recenziju", color = Color.White)
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
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
                                onValueChange = { reviewText = it },
                                label = { Text("Recenzija") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Ocena:")
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (i in 1..5) {
                                    IconButton(
                                        onClick = { rating = i },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        androidx.compose.material3.Icon(
                                            painter = painterResource(id = if (i <= rating) R.drawable.star_filled_icon else R.drawable.star_outline_icon),
                                            contentDescription = null,
                                            tint = if (i <= rating) Color.Yellow else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            FirebaseObject.addReview(cafeRestaurantID,reviewText,rating)
                            showReviewDialog = false
                        }) {
                            Text("Potvrdi")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
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
                        Button(onClick = {
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
                        Button(onClick = { showCrowdDialog = false }) {
                            Text("Otkaži")
                        }
                    }
                )
            }

        }
    }
