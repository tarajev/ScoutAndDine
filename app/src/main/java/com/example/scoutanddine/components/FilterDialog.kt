package com.example.scoutanddine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scoutanddine.screens.RatingBar

@Composable
fun FilterDialog(
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    selectedRating: Int,
    onRatingChanged: (Int) -> Unit,
    onlyAvailable: Boolean,
    onAvailableChanged: (Boolean) -> Unit,
    searchRadius: Float,
    onRadiusChanged: (Float) -> Unit,
    onApplyFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Filteri", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Tags (Kafić, Restoran)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedTag == "All",
                        onClick = { onTagSelected("All") },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedTag == "Kafi",
                        onClick = { onTagSelected("Kafi") },
                        label = { Text("Kafić") }
                    )
                    FilterChip(
                        selected = selectedTag == "Restoran",
                        onClick = { onTagSelected("Restoran") },
                        label = { Text("Restoran") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Filter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Min. Rating:")
                    Spacer(modifier = Modifier.width(8.dp))
                    RatingBar(rating = selectedRating, onRatingChange = onRatingChanged)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Only Available Places Filter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = onlyAvailable,
                        onCheckedChange = onAvailableChanged
                    )
                    Text("Samo dostupni objekti")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Radius Search
                Text("Pretraga u radijusu (u km): $searchRadius")
                Slider(
                    value = searchRadius,
                    onValueChange = onRadiusChanged,
                    valueRange = 0f..50f,
                    steps = 100,
                    colors = SliderDefaults.colors(
                        thumbColor =  Color(51, 204, 255),  // Boja dugmeta (klizača)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Apply Button
                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =   Color(51, 204, 255),  // Boja pozadine dugmeta
                        contentColor = Color.White // Boja teksta unutar dugmeta
                    )
                ) {
                    Text("Primeni")
                }
            }
        }
    }
}