package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AuthTab
import com.example.data.models.PaymentOperator
import com.example.ui.EasyTransportViewModel
import com.example.ui.UserRole
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    viewModel: EasyTransportViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val authState by viewModel.authState.collectAsState()
    val companies by viewModel.companies.collectAsState()

    var activeTab by remember { mutableStateOf(authState.authTab) }

    // Login fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register fields
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("+225 ") }
    var registerPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var selectedCompanyId by remember { mutableStateOf("comp_cte") }
    var selectedOperator by remember { mutableStateOf(PaymentOperator.WAVE) }
    var registerRole by remember { mutableStateOf(UserRole.VOYAGEUR) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("auth_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header Bar
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "Easy Transport CI",
                            tint = NavyDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AUTHENTIFICATION FIREBASE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandAmber,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Easy Transport CI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
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
                        contentDescription = "Fermer",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Tabs : Connexion / Inscription / Mode Démo
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = NavyDark,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                        color = BrandAmber,
                        height = 3.dp
                    )
                }
            ) {
                AuthTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = {
                            activeTab = tab
                            viewModel.clearAuthMessages()
                        },
                        text = {
                            Text(
                                text = when (tab) {
                                    AuthTab.LOGIN -> "🔑 Connexion"
                                    AuthTab.REGISTER -> "📝 Inscription"
                                    AuthTab.DEMO_VISITOR -> "👀 Mode Démo"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) BrandAmber else Color(0xFFCBD5E1)
                            )
                        }
                    )
                }
            }

            // Error / Success Banner
            if (authState.errorMessage != null) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authState.errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (authState.successMessage != null) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authState.successMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Content per tab
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CreamBackground)
            ) {
                when (activeTab) {
                    AuthTab.LOGIN -> LoginForm(
                        email = loginEmail,
                        password = loginPassword,
                        passwordVisible = loginPasswordVisible,
                        isLoading = authState.isLoading,
                        onEmailChange = { loginEmail = it },
                        onPasswordChange = { loginPassword = it },
                        onTogglePasswordVisibility = { loginPasswordVisible = !loginPasswordVisible },
                        onSubmit = {
                            viewModel.signInWithFirebase(loginEmail, loginPassword) {
                                onDismiss()
                            }
                        },
                        onQuickDemoClick = {
                            activeTab = AuthTab.DEMO_VISITOR
                        },
                        onSwitchToRegister = {
                            activeTab = AuthTab.REGISTER
                        }
                    )

                    AuthTab.REGISTER -> RegisterForm(
                        name = registerName,
                        email = registerEmail,
                        phone = registerPhone,
                        password = registerPassword,
                        passwordVisible = registerPasswordVisible,
                        selectedCompanyId = selectedCompanyId,
                        selectedOperator = selectedOperator,
                        selectedRole = registerRole,
                        companies = companies,
                        isLoading = authState.isLoading,
                        onNameChange = { registerName = it },
                        onEmailChange = { registerEmail = it },
                        onPhoneChange = { registerPhone = it },
                        onPasswordChange = { registerPassword = it },
                        onTogglePasswordVisibility = { registerPasswordVisible = !registerPasswordVisible },
                        onCompanyChange = { selectedCompanyId = it },
                        onOperatorChange = { selectedOperator = it },
                        onRoleChange = { registerRole = it },
                        onSubmit = {
                            viewModel.registerWithFirebase(
                                name = registerName,
                                email = registerEmail,
                                phone = registerPhone,
                                password = registerPassword,
                                companyId = selectedCompanyId,
                                operator = selectedOperator,
                                role = registerRole
                            ) {
                                onDismiss()
                            }
                        },
                        onSwitchToLogin = {
                            activeTab = AuthTab.LOGIN
                        }
                    )

                    AuthTab.DEMO_VISITOR -> DemoVisitorSection(
                        onSelectDemoRole = { role ->
                            viewModel.enterDemoMode(role)
                            onDismiss()
                        },
                        onSwitchToRegister = {
                            activeTab = AuthTab.REGISTER
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onQuickDemoClick: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, CreamBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CONNEXION À VOTRE COMPTE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                    Text(
                        text = "Retrouvez vos billets, points fidélité et préférences de voyage.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Adresse Email") },
                        placeholder = { Text("ex: passager@easytransport.ci") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NavyDark)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandAmber,
                            unfocusedBorderColor = CreamBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_email")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Mot de passe") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NavyDark)
                        },
                        trailingIcon = {
                            IconButton(onClick = onTogglePasswordVisibility) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Masquer" else "Afficher",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandAmber,
                            unfocusedBorderColor = CreamBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_password")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onSubmit,
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark, contentColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_login")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = BrandAmber,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connexion Firebase en cours...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SE CONNECTER AVEC FIREBASE", fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shortcuts for quick test logins
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Identifiants rapides :",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Surface(
                            onClick = {
                                onEmailChange("valence@easytransport.ci")
                                onPasswordChange("123456")
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Text(
                                text = "Remplir Démo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Switch to visitor mode or create account
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOUS VISITEZ SEULEMENT L'APPLICATION ?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Accédez immédiatement sans mot de passe pour tester la réservation, le GPS et l'IA vocale.",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onQuickDemoClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF92400E)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF92400E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_switch_to_demo_mode")
                    ) {
                        Text("EXPLORER EN MODE DÉMO (VISITEUR)", fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pas encore de compte ? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text = "Créer un compte",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = NavyDark,
                    modifier = Modifier.clickable { onSwitchToRegister() }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegisterForm(
    name: String,
    email: String,
    phone: String,
    password: String,
    passwordVisible: Boolean,
    selectedCompanyId: String,
    selectedOperator: PaymentOperator,
    selectedRole: UserRole,
    companies: List<com.example.data.models.TransportCompany>,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onCompanyChange: (String) -> Unit,
    onOperatorChange: (PaymentOperator) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onSubmit: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, CreamBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CRÉATION DE COMPTE FIREBASE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                    Text(
                        text = "Rejoignez le réseau national Easy Transport Côte d'Ivoire.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nom et Prénoms") },
                        placeholder = { Text("ex: Kouassi Valence") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NavyDark)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_register_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Adresse Email") },
                        placeholder = { Text("ex: kouassi@gmail.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NavyDark)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_register_email")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = { Text("Numéro Mobile Money (WhatsApp)") },
                        placeholder = { Text("+225 07 12 34 56 78") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = NavyDark)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_register_phone")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Mot de passe (au moins 6 caractères)") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NavyDark)
                        },
                        trailingIcon = {
                            IconButton(onClick = onTogglePasswordVisibility) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_register_password")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Company Preference
                    Text(
                        text = "COMPAGNIE PRÉFÉRÉE (POUR VOTRE COMPTE) :",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        companies.forEach { company ->
                            val isSelected = company.id == selectedCompanyId
                            Surface(
                                onClick = { onCompanyChange(company.id) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NavyDark else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) BrandAmber else CreamBorder)
                            ) {
                                Text(
                                    text = company.shortName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) BrandAmber else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preferred Mobile Money
                    Text(
                        text = "OPÉRATEUR MOBILE MONEY PAR DÉFAUT :",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentOperator.entries.forEach { op ->
                            val isOpSelected = op == selectedOperator
                            Surface(
                                onClick = { onOperatorChange(op) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isOpSelected) NavyDark else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isOpSelected) BrandAmber else CreamBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = op.displayName.substringBefore(" "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOpSelected) BrandAmber else TextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onSubmit,
                        enabled = !isLoading && name.isNotBlank() && email.isNotBlank() && password.length >= 6,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber, contentColor = NavyDark),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_register")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = NavyDark, modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Création du compte Firebase...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("S'INSCRIRE AVEC FIREBASE (+150 PTS BONUS)", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vous avez déjà un compte ? ", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text = "Se connecter",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = NavyDark,
                    modifier = Modifier.clickable { onSwitchToLogin() }
                )
            }
        }
    }
}

@Composable
private fun DemoVisitorSection(
    onSelectDemoRole: (UserRole) -> Unit,
    onSwitchToRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, CreamBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MODE DÉMO / VISITEUR",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = NavyDark
                            )
                            Text(
                                text = "Accès instantané sans création de compte",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = CreamBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "En tant que visiteur, vous pouvez explorer librement toutes les fonctionnalités :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Consulter les départs de toutes les compagnies (CTE, UTB, STIF...)", fontSize = 12.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Suivi GPS en temps réel des cars sur les axes routiers", fontSize = 12.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tester la simulation de réservation CinetPay & génération de QR billet", fontSize = 12.sp, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Poser des questions à l'Assistant Vocal IA EasyTransport", fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 3 Demo Role Cards
        item {
            Text(
                text = "CHOISISSEZ UN PROFIL DE TEST :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, BrandAmber),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDemoRole(UserRole.VOYAGEUR) }
                    .testTag("card_demo_role_voyageur")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Passager / Voyageur", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text("Recommandé", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                            Text("Rechercher, réserver, suivre son car en direct", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = NavyDark, modifier = Modifier.size(20.dp))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CreamBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDemoRole(UserRole.CHAUFFEUR) }
                    .testTag("card_demo_role_chauffeur")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F766E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Chauffeur de Car", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("Scanner les QR codes, GPS car, alertes trafic", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CreamBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDemoRole(UserRole.GARE) }
                    .testTag("card_demo_role_gare")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFB45309)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Chef de Gare / Régulateur", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("Gestion des départs et suivi de flotte", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onSwitchToRegister,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CRÉER UN VRAI COMPTE FIREBASE", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
