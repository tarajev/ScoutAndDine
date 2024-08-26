package com.example.scoutanddine.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.scoutanddine.data.FirebaseObject.fetchUsers
import com.example.scoutanddine.data.entities.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(navController: NavController) {
    var users by remember { mutableStateOf<List<User>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch users
    LaunchedEffect(Unit) {
        fetchUsers(
            onSuccess = { fetchedUsers ->
                users = fetchedUsers
                isLoading = false
            },
            onFailure = { e ->
                errorMessage = e.message
                isLoading = false
            }
        )
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (errorMessage != null) {
        Text(text = "Error: $errorMessage", color = Color.Red, modifier = Modifier.fillMaxSize())
    } else {
        TopAppBar(
            title = {
                Text(
                    text = "Leaderboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            colors = TopAppBarColors(Color(51,204, 255), Color(51,204, 255),Color(51,204, 255), Color(51,204, 255), Color(51,204, 255)),
            modifier = Modifier.shadow(4.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(0.dp,64.dp,0.dp,0.dp)) {
            items(users ?: emptyList()) { user ->
                val rank = users?.indexOf(user)?.plus(1) ?: 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(user.image),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            //.clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${user.points} points", fontSize = 14.sp, color = Color.Gray)
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                when (rank) {
                                    1 -> Color(0xFFFFD700) // Gold for 1st place
                                    2 -> Color(0xFFC0C0C0) // Silver for 2nd place
                                    3 -> Color(0xFFCD7F32) // Bronze for 3rd place
                                    else -> Color(0xFF4CAF50) // Green for other places
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rank.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
