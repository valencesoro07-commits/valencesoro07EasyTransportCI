package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Trip
import com.example.data.models.TripStatus
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.CreamCardVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TripCard(
    trip: Trip,
    onBookClick: () -> Unit,
    onTrackGpsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedPrice = NumberFormat.getNumberInstance(Locale.FRENCH).format(trip.price)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.5.dp, CreamBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Company Name & Bus Type & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = trip.companyName,
                            tint = BrandAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = trip.companyName,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${trip.busType} • ${trip.busPlateNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }

                // Status Badge (High Contrast with colored border)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (trip.status) {
                        TripStatus.IN_TRANSIT -> Color(0xFFDCFCE7)
                        TripStatus.BOARDING -> Color(0xFFFEF3C7)
                        TripStatus.SCHEDULED -> Color(0xFFF1F5F9)
                        TripStatus.COMPLETED -> Color(0xFFE2E8F0)
                        TripStatus.DELAYED -> Color(0xFFFEE2E2)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (trip.status) {
                            TripStatus.IN_TRANSIT -> Color(0xFF86EFAC)
                            TripStatus.BOARDING -> Color(0xFFFDE68A)
                            TripStatus.SCHEDULED -> Color(0xFFCBD5E1)
                            TripStatus.COMPLETED -> Color(0xFF94A3B8)
                            TripStatus.DELAYED -> Color(0xFFFCA5A5)
                        }
                    )
                ) {
                    Text(
                        text = when (trip.status) {
                            TripStatus.IN_TRANSIT -> "● En Route"
                            TripStatus.BOARDING -> "● Embarquement"
                            TripStatus.SCHEDULED -> "Prévu"
                            TripStatus.COMPLETED -> "Arrivé"
                            TripStatus.DELAYED -> "Retardé"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = when (trip.status) {
                            TripStatus.IN_TRANSIT -> Color(0xFF166534)
                            TripStatus.BOARDING -> Color(0xFFB45309)
                            TripStatus.SCHEDULED -> Color(0xFF334155)
                            TripStatus.COMPLETED -> Color(0xFF475569)
                            TripStatus.DELAYED -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Route & Timings Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.departureTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = trip.departureCity,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = trip.departureStation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                // Duration Indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = trip.durationText,
                        fontSize = 11.sp,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BrandOrange)
                        )
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(2.5.dp)
                                .background(BrandOrange)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = BrandOrange,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (trip.status == TripStatus.IN_TRANSIT) {
                        Text(
                            text = "${trip.speedKmH} km/h (GPS)",
                            fontSize = 10.sp,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Arrival
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = trip.arrivalTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = trip.arrivalCity,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = trip.arrivalStation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = CreamBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Footer: Seats remaining + Price + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$formattedPrice FCFA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AirlineSeatReclineNormal,
                            contentDescription = null,
                            tint = if (trip.availableSeats > 5) Color(0xFF059669) else Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${trip.availableSeats} places dispo",
                            fontSize = 11.sp,
                            color = if (trip.availableSeats > 5) Color(0xFF047857) else Color(0xFFB91C1C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Track GPS button
                    OutlinedButton(
                        onClick = onTrackGpsClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, NavyDark),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF8FAFC)
                        ),
                        modifier = Modifier.testTag("btn_track_gps_${trip.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Suivi GPS",
                            tint = NavyDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }

                    // Book with CinetPay
                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandAmber,
                            contentColor = NavyDark
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier.testTag("btn_book_trip_${trip.id}")
                    ) {
                        Text(
                            text = "RÉSERVER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }
                }
            }
        }
    }
}
