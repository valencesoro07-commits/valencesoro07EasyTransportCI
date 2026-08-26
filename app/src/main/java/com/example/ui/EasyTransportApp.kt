package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AuthDialog
import com.example.ui.components.CinetPaySheet
import com.example.ui.components.TicketDetailDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.components.UserMenuSheet
import com.example.ui.screens.DriverStationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveGpsScreen
import com.example.ui.screens.MistralVoiceScreen
import com.example.ui.screens.MyTicketsScreen
import com.example.ui.screens.TripsSearchScreen
import com.example.ui.screens.WelcomeAuthScreen
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EasyTransportApp(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val isUserMenuOpen by viewModel.isUserMenuOpen.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // 1. If user has not logged in, registered, or chosen demo mode yet, show full-screen WelcomeAuthScreen
    if (!authState.isSessionStarted) {
        WelcomeAuthScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    // 2. Main Application Scaffold once authenticated or in Demo Visitor mode
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = CreamBackground,
        topBar = {
            TopHeaderBar(
                currentRole = currentRole,
                onRoleChange = { viewModel.setRole(it) },
                onVoiceClick = { viewModel.setTab(NavTab.VOICE) },
                onNotificationClick = { /* Notifications */ },
                onMenuClick = { viewModel.openUserMenu() },
                authState = authState,
                onAuthClick = { viewModel.returnToAuthScreen() }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
            ) {
                // Background Navigation Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(68.dp)
                        .shadow(12.dp),
                    color = NavyDark,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home
                        BottomNavItem(
                            icon = Icons.Default.Home,
                            label = "Accueil",
                            isSelected = currentTab == NavTab.HOME,
                            onClick = { viewModel.setTab(NavTab.HOME) },
                            testTag = "nav_home"
                        )

                        // Trips
                        BottomNavItem(
                            icon = Icons.Default.DirectionsBus,
                            label = "Trajets",
                            isSelected = currentTab == NavTab.TRIPS,
                            onClick = { viewModel.setTab(NavTab.TRIPS) },
                            testTag = "nav_trips"
                        )

                        // Space for floating center mic
                        Spacer(modifier = Modifier.width(56.dp))

                        // GPS Radar
                        BottomNavItem(
                            icon = Icons.Default.GpsFixed,
                            label = "Suivi GPS",
                            isSelected = currentTab == NavTab.GPS,
                            onClick = { viewModel.setTab(NavTab.GPS) },
                            testTag = "nav_gps"
                        )

                        // My Tickets / Driver Mode
                        if (currentRole == UserRole.VOYAGEUR) {
                            BottomNavItem(
                                icon = Icons.Default.ConfirmationNumber,
                                label = "Billets",
                                isSelected = currentTab == NavTab.TICKETS,
                                onClick = { viewModel.setTab(NavTab.TICKETS) },
                                testTag = "nav_tickets"
                            )
                        } else {
                            BottomNavItem(
                                icon = Icons.Default.Store,
                                label = "Chauffeur",
                                isSelected = currentTab == NavTab.DRIVER_STATION,
                                onClick = { viewModel.setTab(NavTab.DRIVER_STATION) },
                                testTag = "nav_driver_station"
                            )
                        }
                    }
                }

                // Center Floating Action Button (Voice Assistant AI)
                FloatingActionButton(
                    onClick = { viewModel.setTab(NavTab.VOICE) },
                    shape = CircleShape,
                    containerColor = BrandAmber,
                    contentColor = NavyDark,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .size(58.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 0.dp)
                        .testTag("floating_voice_center_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Assistant Vocal",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "nav_transition"
            ) { tab ->
                when (tab) {
                    NavTab.HOME -> HomeScreen(viewModel = viewModel)
                    NavTab.TRIPS -> TripsSearchScreen(viewModel = viewModel)
                    NavTab.GPS -> LiveGpsScreen(viewModel = viewModel)
                    NavTab.VOICE -> MistralVoiceScreen(viewModel = viewModel)
                    NavTab.TICKETS -> MyTicketsScreen(viewModel = viewModel)
                    NavTab.DRIVER_STATION -> DriverStationScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Firebase Authentication & Demo Mode Dialog
    if (authState.showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeAuthDialog() }
        )
    }

    // User Profile & All Companies Menu Sheet (Trois petits traits)
    if (isUserMenuOpen) {
        UserMenuSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.closeUserMenu() }
        )
    }

    // CinetPay Payment Sheet
    if (bookingState.showPaymentSheet && bookingState.selectedTrip != null) {
        CinetPaySheet(
            trip = bookingState.selectedTrip!!,
            bookingState = bookingState,
            onNameChange = { viewModel.updateBookingForm(name = it) },
            onPhoneChange = { viewModel.updateBookingForm(phone = it) },
            onEmailChange = { viewModel.updateBookingForm(email = it) },
            onSeatSelect = { viewModel.updateBookingForm(seat = it) },
            onOperatorSelect = { viewModel.updateBookingForm(operator = it) },
            onPickupTypeSelect = { viewModel.updateBookingForm(pickupType = it) },
            onConfirmPayment = { viewModel.executeCinetPayPayment() },
            onDismiss = { viewModel.dismissPaymentSheet() }
        )
    }

    // Success Booking Modal
    if (bookingState.showSuccessDialog && bookingState.lastBookedTicket != null) {
        val ticket = bookingState.lastBookedTicket!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissSuccessDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paiement CinetPay Validé !", fontWeight = FontWeight.Black, fontSize = 17.sp, color = NavyDark)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Votre billet ${ticket.companyName} (${ticket.departureCity} - ${ticket.arrivalCity}) a été émis avec succès !",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("✅ Confirmation envoyée par SMS au ${ticket.passengerPhone}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            Text("✅ Billet PDF & QR Code envoyés à ${ticket.passengerEmail}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Réf : ${ticket.bookingReference} • Siège N°${ticket.seatNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissSuccessDialog()
                        viewModel.setSelectedTicketDetail(ticket)
                        viewModel.setTab(NavTab.TICKETS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                ) {
                    Text("Voir mon Billet & QR Code", color = NavyDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.dismissSuccessDialog()
                        viewModel.setTab(NavTab.GPS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Suivre le car (GPS)", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) BrandAmber else Color(0xFFCBD5E1),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BrandAmber else Color(0xFFCBD5E1)
        )
    }
}
