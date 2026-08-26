package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AuthTab
import com.example.data.models.AuthUiState
import com.example.data.models.PaymentOperator
import com.example.data.models.TransportCompany
import com.example.ui.EasyTransportViewModel
import com.example.ui.UserRole
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MoovMoneyColor
import com.example.ui.theme.MtnMoneyColor
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaveColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuSheet(
    viewModel: EasyTransportViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val companies by viewModel.companies.collectAsState()
    val favoriteCompanyId by viewModel.favoriteCompanyId.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var companySearchQuery by remember { mutableStateOf("") }
    var expandedCompanyId by remember { mutableStateOf<String?>(null) }
    var isEditingProfile by remember { mutableStateOf(false) }

    // Form inputs for profile editing
    var editName by remember(bookingState.passengerName) { mutableStateOf(bookingState.passengerName) }
    var editPhone by remember(bookingState.passengerPhone) { mutableStateOf(bookingState.passengerPhone) }
    var editEmail by remember(bookingState.passengerEmail) { mutableStateOf(bookingState.passengerEmail) }
    var editOperator by remember(bookingState.selectedOperator) { mutableStateOf(bookingState.selectedOperator) }

    val favoriteCompany = companies.find { it.id == favoriteCompanyId } ?: companies.firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("user_menu_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header Bar with User summary & Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NavyDark, NavySurface)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (authState.isLoggedIn) EmeraldSuccess else BrandAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = editName.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (authState.isLoggedIn) Color.White else NavyDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = editName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (authState.isLoggedIn) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Compte Firebase (${authState.currentUser?.loyaltyPoints ?: 150} pts)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = BrandAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mode Démo (Visiteur)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandAmber
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NavyLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer le menu",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Navigation Tabs inside Menu
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavyDark,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandAmber,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "🏢 Compagnies (${companies.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Medium,
                            color = if (selectedTab == 0) BrandAmber else Color(0xFFCBD5E1)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "👤 Mon Profil",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Medium,
                            color = if (selectedTab == 1) BrandAmber else Color(0xFFCBD5E1)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "⭐ Compagnie Favorite",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Black else FontWeight.Medium,
                            color = if (selectedTab == 2) BrandAmber else Color(0xFFCBD5E1)
                        )
                    }
                )
            }

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CreamBackground)
            ) {
                when (selectedTab) {
                    0 -> CompaniesListTab(
                        companies = companies,
                        favoriteCompanyId = favoriteCompanyId,
                        searchQuery = companySearchQuery,
                        onSearchQueryChange = { companySearchQuery = it },
                        expandedCompanyId = expandedCompanyId,
                        onToggleExpand = { id ->
                            expandedCompanyId = if (expandedCompanyId == id) null else id
                        },
                        onSetFavorite = { id ->
                            viewModel.setFavoriteCompany(id)
                        },
                        onBookWithCompany = { company ->
                            onDismiss()
                            viewModel.selectCompanyForTrips(company.id, company.name)
                        }
                    )
                    1 -> ProfileTab(
                        name = editName,
                        phone = editPhone,
                        email = editEmail,
                        operator = editOperator,
                        isEditing = isEditingProfile,
                        ticketsCount = tickets.size,
                        favoriteCompany = favoriteCompany,
                        currentRole = currentRole,
                        authState = authState,
                        onRoleChange = { viewModel.setRole(it) },
                        onEditToggle = { isEditingProfile = !isEditingProfile },
                        onNameChange = { editName = it },
                        onPhoneChange = { editPhone = it },
                        onEmailChange = { editEmail = it },
                        onOperatorChange = { editOperator = it },
                        onOpenAuth = { tab ->
                            onDismiss()
                            viewModel.returnToAuthScreen(tab)
                        },
                        onSignOut = {
                            viewModel.signOutFromFirebase()
                        },
                        onEnterDemoRole = { role ->
                            viewModel.enterDemoMode(role)
                        },
                        onSave = {
                            viewModel.updateUserProfile(editName, editPhone, editEmail, editOperator)
                            isEditingProfile = false
                        }
                    )
                    2 -> FavoriteCompanyDetailTab(
                        favoriteCompany = favoriteCompany,
                        onBook = {
                            if (favoriteCompany != null) {
                                onDismiss()
                                viewModel.selectCompanyForTrips(favoriteCompany.id, favoriteCompany.name)
                            }
                        },
                        onExploreOtherCompanies = {
                            selectedTab = 0
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompaniesListTab(
    companies: List<TransportCompany>,
    favoriteCompanyId: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    expandedCompanyId: String?,
    onToggleExpand: (String) -> Unit,
    onSetFavorite: (String) -> Unit,
    onBookWithCompany: (TransportCompany) -> Unit
) {
    val filtered = companies.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.shortName.contains(searchQuery, ignoreCase = true) ||
                it.headquarterCity.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("Rechercher CTE, UTB, AVS, STIF, Man, Bouaké...", fontSize = 13.sp, color = TextSecondary)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NavyDark)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandAmber,
                    unfocusedBorderColor = CreamBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("menu_search_company_input")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMPAGNIES DE TRANSPORT AGRÉÉES CI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${filtered.size} disponibles",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )
            }
        }

        items(filtered, key = { it.id }) { company ->
            val isFavorite = company.id == favoriteCompanyId
            val isExpanded = company.id == expandedCompanyId

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(if (isFavorite) 2.dp else 1.5.dp, if (isFavorite) BrandAmber else CreamBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isFavorite) 4.dp else 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("menu_company_item_${company.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isFavorite) BrandAmber else NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = company.shortName.take(3),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (isFavorite) NavyDark else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = company.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "Gares : ${company.headquarterCity}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Star Favorite Button
                        IconButton(
                            onClick = { onSetFavorite(company.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isFavorite) Color(0xFFFEF3C7) else Color(0xFFF1F5F9))
                                .testTag("btn_favorite_${company.id}")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Compagnie préférée" else "Définir comme préférée",
                                tint = if (isFavorite) Color(0xFFDC2626) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isFavorite) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VOTRE COMPAGNIE PRÉFÉRÉE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating & Fleet summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${company.rating} (${company.reviewCount} avis)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Flotte : ${company.fleetCount} autocars",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        // Toggle Info details
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleExpand(company.id) }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "Moins d'infos" else "Plus d'infos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Expanded Details Section
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Divider(color = CreamBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = company.description,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "SERVICES & CONFORT À BORD :",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                company.amenities.forEach { amenity ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            text = "✓ $amenity",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Infoline & Gare : ${company.phone}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Button : Réserver un billet chez cette compagnie
                    Button(
                        onClick = { onBookWithCompany(company) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFavorite) BrandAmber else NavyDark,
                            contentColor = if (isFavorite) NavyDark else Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_book_company_${company.id}")
                    ) {
                        Text(
                            text = "RÉSERVER UN BILLET CHEZ ${company.shortName.uppercase()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(
    name: String,
    phone: String,
    email: String,
    operator: PaymentOperator,
    isEditing: Boolean,
    ticketsCount: Int,
    favoriteCompany: TransportCompany?,
    currentRole: UserRole,
    authState: AuthUiState,
    onRoleChange: (UserRole) -> Unit,
    onEditToggle: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onOperatorChange: (PaymentOperator) -> Unit,
    onOpenAuth: (AuthTab) -> Unit,
    onSignOut: () -> Unit,
    onEnterDemoRole: (UserRole) -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Authentication Status Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (authState.isLoggedIn) Color(0xFFF0FDF4) else Color(0xFFFEF3C7)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (authState.isLoggedIn) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (authState.isLoggedIn) EmeraldSuccess else Color(0xFFD97706)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (authState.isLoggedIn) Icons.Default.CheckCircle else Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (authState.isLoggedIn) "COMPTE FIREBASE CONNECTÉ" else "MODE DÉMO (VISITEUR)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (authState.isLoggedIn) Color(0xFF166534) else Color(0xFF92400E),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (authState.isLoggedIn) (authState.currentUser?.email ?: "Session active") else "Session Invité / Démo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        if (authState.isLoggedIn) {
                            Surface(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEE2E2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                            ) {
                                Text(
                                    text = "Déconnexion",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!authState.isLoggedIn) {
                        Text(
                            text = "Vous explorez l'application en mode visiteur. Pour sauvegarder vos vrais billets et cumuler des points, créez un compte ou connectez-vous.",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onOpenAuth(AuthTab.LOGIN) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Se Connecter", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = { onOpenAuth(AuthTab.REGISTER) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber, contentColor = NavyDark),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("S'inscrire", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
        // User Main Info Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, CreamBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = BrandAmber
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = phone,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Text(
                                    text = email,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        IconButton(
                            onClick = onEditToggle,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Modifier",
                                tint = NavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Editing form if active
                    AnimatedVisibility(visible = isEditing) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Divider(color = CreamBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("MODIFIER VOS INFORMATIONS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = onNameChange,
                                label = { Text("Nom et Prénoms") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = onPhoneChange,
                                label = { Text("Numéro Mobile Money (WhatsApp/SMS)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = onEmailChange,
                                label = { Text("Email pour réception des e-billets") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("MOYEN DE PAIEMENT PRÉFÉRÉ :", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PaymentOperator.entries.forEach { op ->
                                    val isOpSelected = op == operator
                                    Surface(
                                        onClick = { onOperatorChange(op) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isOpSelected) NavyDark else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isOpSelected) BrandAmber else CreamBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = op.displayName.substringBefore(" "),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isOpSelected) BrandAmber else TextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onSave,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber, contentColor = NavyDark),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ENREGISTRER LES MODIFICATIONS", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Stats Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CreamBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("BILLETS ÉMIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$ticketsCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CreamBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("POINTS FIDÉLITÉ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("450 pts", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CreamBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("STATUT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gold", fontSize = 20.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                    }
                }
            }
        }

        // Favorite Company highlight
        if (favoriteCompany != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.5.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = favoriteCompany.shortName.take(3),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = BrandAmber
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("COMPAGNIE FAVORITE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E))
                                Text(favoriteCompany.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            }
                        }
                    }
                }
            }
        }

        // Switch App Mode
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CreamBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("MODE D'UTILISATION DE L'APPLICATION :", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.entries.forEach { role ->
                            val isSelected = role == currentRole
                            Surface(
                                onClick = { onRoleChange(role) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NavyDark else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) BrandAmber else CreamBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = role.label.substringBefore(" "),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BrandAmber else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FavoriteCompanyDetailTab(
    favoriteCompany: TransportCompany?,
    onBook: () -> Unit,
    onExploreOtherCompanies: () -> Unit
) {
    if (favoriteCompany == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Aucune compagnie favorite sélectionnée.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onExploreOtherCompanies,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text("Choisir une compagnie", color = Color.White)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, BrandAmber),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = favoriteCompany.shortName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandAmber
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = favoriteCompany.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Réseau : ${favoriteCompany.headquarterCity}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Favorite", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = favoriteCompany.description,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = CreamBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("ÉQUIPEMENTS & FLOTTE VIP :", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        favoriteCompany.amenities.forEach { amenity ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = "✓ $amenity",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(favoriteCompany.phone, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Text("${favoriteCompany.fleetCount} Cars en circulation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBook,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber, contentColor = NavyDark),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_book_favorite_company_direct")
                    ) {
                        Text(
                            text = "RÉSERVER MAINTENANT AVEC ${favoriteCompany.shortName.uppercase()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onExploreOtherCompanies,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Explorer les autres compagnies du réseau", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}
