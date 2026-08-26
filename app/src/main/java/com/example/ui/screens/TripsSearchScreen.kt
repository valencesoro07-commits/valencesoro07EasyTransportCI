package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EasyTransportViewModel
import com.example.ui.components.SearchBarSection
import com.example.ui.components.TripCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TripsSearchScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val filteredTrips by viewModel.filteredTrips.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCity by viewModel.selectedDepartureCity.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("screen_trips_search")
    ) {
        // Search & Filter header
        SearchBarSection(
            searchQuery = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            selectedCity = selectedCity,
            onCitySelect = { city ->
                if (city == "Toutes") {
                    viewModel.filterByRoute("Toutes", "Toutes")
                } else {
                    viewModel.filterByRoute(city, "Toutes")
                }
            }
        )

        // Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredTrips.size} départs disponibles",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyDark
            )
            if (searchQuery.isNotBlank() || selectedCity != "Toutes") {
                Surface(
                    onClick = {
                        viewModel.setSearchQuery("")
                        viewModel.filterByRoute("Toutes", "Toutes")
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Réinitialiser filtres",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // List of Trips
        if (filteredTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucun car trouvé pour cette recherche",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Essayez avec une autre ville ou une autre compagnie",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.filterByRoute("Toutes", "Toutes")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                    ) {
                        Text("Afficher tous les trajets", color = BrandAmber)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTrips) { trip ->
                    TripCard(
                        trip = trip,
                        onBookClick = { viewModel.startBooking(trip) },
                        onTrackGpsClick = { viewModel.setTrackedTrip(trip) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}
