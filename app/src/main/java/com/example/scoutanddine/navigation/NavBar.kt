package com.example.scoutanddine.navigation

import MainScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.scoutanddine.screens.LeaderboardScreen
import com.example.scoutanddine.screens.ObjectDetailsScreen
import com.example.scoutanddine.screens.ProfileScreen
import com.example.scoutanddine.screens.SearchScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavBar() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        content = {
            NavHost(
                navController = navController,
                startDestination = "home",
            ) {
                composable("home") { MainScreen(navController) }
                composable("details/{cafeID}", arguments = listOf(navArgument("cafeID") { type = NavType.StringType })) { backStackEntry ->
                    val cafeID = backStackEntry.arguments?.getString("cafeID")!!
                    ObjectDetailsScreen(navController, cafeID)
                }
                composable("leaderboard") { LeaderboardScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
                composable("search") { SearchScreen(navController)}
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = false,
            onClick = { navController.navigate("search") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Leaderboard, contentDescription = "Leaderboard") },
            label = { Text("LeaderBoard") },
            selected = false,
            onClick = { navController.navigate("leaderboard") }

        )
    }
}