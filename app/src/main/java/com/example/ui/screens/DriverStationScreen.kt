package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.MessagePriority
import com.example.ui.EasyTransportViewModel
import com.example.ui.UserRole
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.CreamCardVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class RoadsidePassenger(
    val id: String,
    val name: String,
    val phone: String,
    val pickupLocation: String,
    val destination: String,
    val seatNumber: Int,
    val isPaid: Boolean = true,
    var isBoarded: Boolean = false
)

@Composable
fun DriverStationScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val isGpsBroadcasting by viewModel.isDriverGpsBroadcasting.collectAsState()
    val onboardCount by viewModel.driverOnboardCount.collectAsState()
    val dispatchMessages by viewModel.dispatchMessages.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var customMessage by remember { mutableStateOf("") }

    val roadsidePassengers = remember {
        listOf(
            RoadsidePassenger("rp_1", "Konan Yves", "+225 07 44 33 22 11", "Péage d'Attinguié (Sens Nord)", "Bouaké", 18),
            RoadsidePassenger("rp_2", "Diallo Awa", "+225 05 99 88 77 66", "Péage de Singrobo", "Bouaké", 19),
            RoadsidePassenger("rp_3", "Kouamé Jean", "+225 01 22 33 44 55", "Toumodi (Carrefour Station)", "Bouaké", 20),
            RoadsidePassenger("rp_4", "Bamba Seydou", "+225 07 11 22 33 44", "Tiébissou Centre", "Bouaké", 21)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("screen_driver_station")
    ) {
        // Top Header
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
                            .background(if (currentRole == UserRole.CHAUFFEUR) EmeraldSuccess else BrandAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentRole == UserRole.CHAUFFEUR) Icons.Default.DirectionsBus else Icons.Default.Store,
                            contentDescription = null,
                            tint = NavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentRole == UserRole.CHAUFFEUR) "Cockpit Chauffeur Car #08" else "Espace Chef de Gare",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "UTB Express • Abidjan ➔ Bouaké (CI-4512-JK01)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Switch Role Pill
                Surface(
                    onClick = {
                        viewModel.setRole(if (currentRole == UserRole.CHAUFFEUR) UserRole.GARE else UserRole.CHAUFFEUR)
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF334155)
                ) {
                    Text(
                        text = if (currentRole == UserRole.CHAUFFEUR) "Mode Gare" else "Mode Chauffeur",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub Navigation Tabs: 0=Passagers & Trajet, 1=Radio & Dispatch Gare, 2=Flotte & Recettes
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavyDark,
                contentColor = BrandAmber,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Passagers & Route", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Radio Gare (${dispatchMessages.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Flotte & Recettes", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Passagers & Route Manifest
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // GPS Broadcasting & Capacity Counters
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CreamBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.GpsFixed,
                                            contentDescription = null,
                                            tint = if (isGpsBroadcasting) EmeraldSuccess else Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isGpsBroadcasting) "Émission GPS : ACTIVE" else "Émission GPS : PAUSE",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isGpsBroadcasting) EmeraldSuccess else Color(0xFFEF4444)
                                            )
                                            Text(
                                                text = "Visible par les voyageurs et le dispatch gare",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = isGpsBroadcasting,
                                        onCheckedChange = { viewModel.toggleDriverGps() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = EmeraldSuccess,
                                            checkedTrackColor = Color(0xFFDCFCE7)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = CreamBorder)
                                Spacer(modifier = Modifier.height(14.dp))

                                // Real-time Passenger Stats Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("À BORD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                        Text("$onboardCount / 52", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NavyDark)
                                        Text("Passagers assis", fontSize = 10.sp, color = EmeraldSuccess)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("SUR LA ROUTE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                        Text("${roadsidePassengers.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                                        Text("À récupérer", fontSize = 10.sp, color = Color(0xFF92400E))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFDCFCE7))
                                            .padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("LIBRES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                        Text("${52 - onboardCount - roadsidePassengers.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                                        Text("Disponibles", fontSize = 10.sp, color = Color(0xFF166534))
                                    }
                                }
                            }
                        }
                    }

                    // Roadside Pickups Section Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Passagers en attente sur l'axe routier",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = NavyDark
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0284C7)
                            ) {
                                Text(
                                    text = "Billets CinetPay Payés",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // List of Roadside Passengers
                    items(roadsidePassengers) { passenger ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CreamBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(NavyLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(passenger.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                            Text("Siège N°${passenger.seatNumber} • Vers ${passenger.destination}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text("Payé Wave", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Arrêt de prise en charge : ${passenger.pickupLocation}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFB45309)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { /* Call passenger */ },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, CreamBorder)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(passenger.phone, fontSize = 11.sp, color = TextPrimary)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.validatePassengerCheckIn("tkt_sample_01")
                                            passenger.isBoarded = true
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (passenger.isBoarded) EmeraldSuccess else BrandAmber
                                        )
                                    ) {
                                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NavyDark, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (passenger.isBoarded) "À BORD" else "VALIDER EMBARQUEMENT",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDark
                                        )
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

            1 -> {
                // Radio & Dispatch Messages Stream
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Quick Action Radio Buttons
                    Text(
                        text = "Transmissions rapides à la Gare",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.sendDriverDispatchMessage("Départ effectué de la gare avec 44 passagers.") },
                            shape = RoundedCornerShape(12.dp),
                            color = NavyDark
                        ) {
                            Text("📢 Départ Effectué", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }

                        Surface(
                            onClick = { viewModel.sendDriverDispatchMessage("Ralentissement constaté au péage.", MessagePriority.DELAY) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFB45309)
                        ) {
                            Text("⏳ Retard / Péage", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }

                        Surface(
                            onClick = { viewModel.sendDriverDispatchMessage("Arrêt pause technique de 5 minutes.", MessagePriority.NORMAL) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0284C7)
                        ) {
                            Text("🛑 Pause 5min", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }

                        Surface(
                            onClick = { viewModel.sendDriverDispatchMessage("Signalement incident ou panne en cours d'évaluation.", MessagePriority.URGENT) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEF4444)
                        ) {
                            Text("🚨 Urgence / Panne", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feed of Messages
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(dispatchMessages) { msg ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (msg.priority) {
                                        MessagePriority.URGENT -> Color(0xFFFEE2E2)
                                        MessagePriority.DELAY -> Color(0xFFFEF3C7)
                                        MessagePriority.NORMAL -> Color.White
                                    }
                                ),
                                border = BorderStroke(1.dp, CreamBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Radio,
                                                contentDescription = null,
                                                tint = if (msg.priority == MessagePriority.URGENT) Color(0xFFEF4444) else BrandAmber,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${msg.senderName} (${msg.senderRole})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(msg.timestamp, fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.message,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Send dispatch message input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 70.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customMessage,
                            onValueChange = { customMessage = it },
                            placeholder = { Text("Message radio pour la gare...", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandAmber,
                                unfocusedBorderColor = CreamBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (customMessage.isNotBlank()) {
                                    viewModel.sendDriverDispatchMessage(customMessage)
                                    customMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(NavyDark)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Envoyer", tint = BrandAmber)
                        }
                    }
                }
            }

            2 -> {
                // Flotte & Recettes (Station Overview)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("RECETTES DU JOUR (CINETPAY MOBILE MONEY)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))
                                Text("2 840 000 FCFA", fontSize = 24.sp, fontWeight = FontWeight.Black, color = BrandAmber)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("384 billets vendus • Taux de remplissage : 89%", fontSize = 11.sp, color = EmeraldSuccess)
                            }
                        }
                    }

                    item {
                        Text("Départs actifs & Suivi de la Flotte", fontWeight = FontWeight.Black, fontSize = 15.sp, color = NavyDark)
                    }

                    items(allTrips) { trip ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CreamBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(trip.companyName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                                        Text("${trip.departureCity} ➔ ${trip.arrivalCity}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    }
                                    Text(trip.departureTime, fontSize = 14.sp, fontWeight = FontWeight.Black, color = BrandAmber)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Position : ${trip.currentStopName} • Vitesse : ${trip.speedKmH} km/h", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
