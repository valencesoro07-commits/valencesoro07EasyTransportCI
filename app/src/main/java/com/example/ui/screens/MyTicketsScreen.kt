package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RateReview
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Ticket
import com.example.ui.EasyTransportViewModel
import com.example.ui.NavTab
import com.example.ui.components.ReviewDialog
import com.example.ui.components.TicketDetailDialog
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.CreamCardVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyTicketsScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val tickets by viewModel.tickets.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val selectedTicketDetail by viewModel.selectedTicketDetail.collectAsState()

    var reviewTicketTarget by remember { mutableStateOf<Ticket?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("screen_my_tickets")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = NavyDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mes Billets de Voyage",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Billets électroniques avec QR Code sécurisé",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF334155)
                ) {
                    Text(
                        text = "${tickets.size} billets",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vous n'avez pas encore de billet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Réservez votre car avec paiement Wave ou Orange Money",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setTab(NavTab.TRIPS) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                    ) {
                        Text("Rechercher un départ", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(tickets) { ticket ->
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.FRENCH).format(ticket.totalPrice)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setSelectedTicketDetail(ticket) }
                            .testTag("ticket_item_${ticket.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, CreamBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = ticket.companyName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = NavyDark
                                    )
                                    Text(
                                        text = "Réf: ${ticket.bookingReference}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandAmber
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Validé (${ticket.paymentOperator.displayName})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Route & Times
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ticket.departureCity, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text(ticket.departureStation, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(ticket.departureTime, fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrandAmber)
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(16.dp))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(ticket.arrivalCity, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Siège N°${ticket.seatNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = CreamBorder, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$formattedPrice FCFA",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { reviewTicketTarget = ticket },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, CreamBorder)
                                    ) {
                                        Icon(Icons.Default.RateReview, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Avis", fontSize = 11.sp, color = TextSecondary)
                                    }

                                    Button(
                                        onClick = {
                                            val trip = allTrips.find { it.id == ticket.tripId } ?: allTrips.firstOrNull()
                                            trip?.let { viewModel.setTrackedTrip(it) }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                                    ) {
                                        Icon(Icons.Default.GpsFixed, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Suivre Car", fontSize = 11.sp, color = BrandAmber, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.setSelectedTicketDetail(ticket) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                                    ) {
                                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = NavyDark, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("QR Code", fontSize = 11.sp, color = NavyDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Ticket Detail Boarding Pass Dialog
    selectedTicketDetail?.let { ticket ->
        TicketDetailDialog(
            ticket = ticket,
            onTrackGpsClick = {
                val trip = allTrips.find { it.id == ticket.tripId } ?: allTrips.firstOrNull()
                trip?.let { viewModel.setTrackedTrip(it) }
                viewModel.setSelectedTicketDetail(null)
            },
            onDismiss = { viewModel.setSelectedTicketDetail(null) }
        )
    }

    // Review Company Dialog
    reviewTicketTarget?.let { ticket ->
        ReviewDialog(
            companyId = ticket.tripId,
            companyName = ticket.companyName,
            onSubmit = { rating, comment, tags ->
                viewModel.submitReview(ticket.tripId, ticket.companyName, rating, comment, tags)
                reviewTicketTarget = null
            },
            onDismiss = { reviewTicketTarget = null }
        )
    }
}
