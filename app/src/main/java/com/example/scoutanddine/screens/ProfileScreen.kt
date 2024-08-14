package com.example.scoutanddine.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.scoutanddine.data.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    var userData by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(uid) {
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(uid).get().await()
            val data = userDoc.toObject(User::class.java)
            userData = data
        }
    }
    userData?.let { user ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color.Cyan, Color.Blue)))
                .padding(16.dp)
        ) {
            // Profile Image and Settings Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = user.image),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.TopCenter)
                )
                IconButton(onClick = { /* Handle settings click */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            // Name and Location
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.name,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Points
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = user.points.toString(),
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Points",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // User Details
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Email",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = user.email,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Username",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = user.username,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Phone",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = user.phone,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

        }
    }
}
