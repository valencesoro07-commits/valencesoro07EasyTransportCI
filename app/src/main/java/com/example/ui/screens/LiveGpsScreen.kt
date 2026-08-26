package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Trip
import com.example.ui.EasyTransportViewModel
import com.example.ui.NavTab
import com.example.ui.components.LiveGpsMapCanvas
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveGpsScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val allTrips by viewModel.allTrips.collectAsState()
    val trackedTrip by viewModel.trackedTrip.collectAsState()
    val activeTrip = trackedTrip ?: allTrips.firstOrNull()

    var showRoadsidePassengers by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_live_gps")
    ) {
        // Fullscreen Live GPS Map Canvas
        LiveGpsMapCanvas(
            activeTrip = activeTrip,
            allTrips = allTrips,
            showRoadsidePassengers = showRoadsidePassengers,
            modifier = Modifier.fillMaxSize()
        )

        // Floating Top Header Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            // Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RADAR GPS EN TEMPS RÉEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            onClick = { showRoadsidePassengers = !showRoadsidePassengers },
                            shape = RoundedCornerShape(8.dp),
                            color = if (showRoadsidePassengers) Color(0xFF0284C7) else Color(0xFF334155)
                        ) {
                            Text(
                                text = if (showRoadsidePassengers) "Arrêts Route : VISIBLES" else "Arrêts Route : MASQUÉS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bus Selector Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allTrips.forEach { trip ->
                            val isSelected = activeTrip?.id == trip.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setTrackedTrip(trip) },
                                label = {
                                    Text(
                                        text = "${trip.companyName} (${trip.departureCity} - ${trip.arrivalCity})",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandAmber,
                                    selectedLabelColor = NavyDark,
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) BrandAmber else Color(0xFF475569),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Bottom Telemetry Card
        if (activeTrip != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.dp, CreamBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Top: Company & Bus Plate & Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = BrandAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeTrip.companyName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Car ${activeTrip.busPlateNumber} • ${activeTrip.busType}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Speed Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeTrip.speedKmH} km/h",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Route Progress Bar & Next Stop
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("POSITION ACTUELLE", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(activeTrip.currentStopName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(16.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("PROCHAIN ARRÊT", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(activeTrip.nextStopName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = CreamBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Passengers load & Roadside Pickups Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AirlineSeatReclineNormal, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activeTrip.totalSeats - activeTrip.availableSeats} / ${activeTrip.totalSeats} à bord",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activeTrip.roadsidePassengersWaiting} passagers en attente sur l'axe",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setTab(NavTab.DRIVER_STATION) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, NavyDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Radio, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Radio Gare", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.startBooking(activeTrip) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("RÉSERVER CE CAR", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
