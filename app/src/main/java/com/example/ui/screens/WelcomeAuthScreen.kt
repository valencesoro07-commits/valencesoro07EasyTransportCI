package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun WelcomeAuthScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val companies by viewModel.companies.collectAsState()

    // false = Écran de Connexion, true = Écran d'Inscription
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login state
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register state
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("+225 ") }
    var registerPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var selectedCompanyId by remember { mutableStateOf("comp_cte") }
    var selectedOperator by remember { mutableStateOf(PaymentOperator.WAVE) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
        ) {
            // 1. Header Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(NavyDark, NavySurface)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Logo Badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(BrandAmber, BrandOrange)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = "Logo",
                                tint = NavyDark,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "EASY TRANSPORT",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BrandAmber
                            ) {
                                Text(
                                    text = "CI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = if (isRegisterMode) "Création d'un nouveau compte" else "Voyagez en toute sérénité partout en Côte d'Ivoire",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 2. Main Authentication Card (Connexion ou Inscription séparée)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, CreamBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        
                        // Titre clair de la section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isRegisterMode) "CRÉER UN COMPTE" else "CONNEXION",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = NavyDark,
                                letterSpacing = 0.5.sp
                            )
                            
                            Surface(
                                onClick = {
                                    isRegisterMode = !isRegisterMode
                                    viewModel.clearAuthMessages()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (isRegisterMode) "Se connecter" else "S'inscrire",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // A. Bouton de Connexion Google Officiel Standard
                        Surface(
                            onClick = {
                                viewModel.signInWithGoogle(context)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_google_signin")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleLogoIcon(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isRegisterMode) "S'inscrire avec Google" else "Continuer avec Google",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // B. Séparateur standard "ou"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE2E8F0),
                                thickness = 1.dp
                            )
                            Text(
                                text = if (isRegisterMode) "ou formulaire d'inscription" else "ou avec votre email",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE2E8F0),
                                thickness = 1.dp
                            )
                        }

                        // Message d'erreur
                        AnimatedVisibility(
                            visible = authState.errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                color = Color(0xFFFEE2E2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = authState.errorMessage ?: "",
                                        fontSize = 11.sp,
                                        color = Color(0xFF991B1B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // D. Formulaires selon l'écran actif
                        if (!isRegisterMode) {
                            // FORMULAIRE DE CONNEXION DÉDIÉ
                            StandardLoginForm(
                                email = loginEmail,
                                password = loginPassword,
                                passwordVisible = loginPasswordVisible,
                                isLoading = authState.isLoading,
                                onEmailChange = { loginEmail = it },
                                onPasswordChange = { loginPassword = it },
                                onTogglePasswordVisibility = { loginPasswordVisible = !loginPasswordVisible },
                                onSubmit = {
                                    viewModel.signInWithFirebase(loginEmail, loginPassword)
                                },
                                onQuickFillDemo = {
                                    loginEmail = "valence@easytransport.ci"
                                    loginPassword = "password123"
                                },
                                onSwitchToRegister = {
                                    isRegisterMode = true
                                    viewModel.clearAuthMessages()
                                }
                            )
                        } else {
                            // FORMULAIRE D'INSCRIPTION DÉDIÉ
                            StandardRegisterForm(
                                name = registerName,
                                email = registerEmail,
                                phone = registerPhone,
                                password = registerPassword,
                                passwordVisible = registerPasswordVisible,
                                selectedCompanyId = selectedCompanyId,
                                selectedOperator = selectedOperator,
                                companies = companies,
                                isLoading = authState.isLoading,
                                onNameChange = { registerName = it },
                                onEmailChange = { registerEmail = it },
                                onPhoneChange = { registerPhone = it },
                                onPasswordChange = { registerPassword = it },
                                onTogglePasswordVisibility = { registerPasswordVisible = !registerPasswordVisible },
                                onCompanyChange = { selectedCompanyId = it },
                                onOperatorChange = { selectedOperator = it },
                                onSubmit = {
                                    viewModel.registerWithFirebase(
                                        name = registerName,
                                        email = registerEmail,
                                        phone = registerPhone,
                                        password = registerPassword,
                                        companyId = selectedCompanyId,
                                        operator = selectedOperator
                                    )
                                },
                                onSwitchToLogin = {
                                    isRegisterMode = false
                                    viewModel.clearAuthMessages()
                                }
                            )
                        }
                    }
                }
            }

            // 3. Mode Démo / Visiteur Card (À sa place initiale en bas !)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.5.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD97706)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "VISITER SANS COMPTE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Explorer toutes les fonctionnalités en mode démo",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.enterDemoMode(UserRole.VOYAGEUR)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF92400E),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_enter_visitor_demo")
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACCÉDER EN MODE DÉMO (VISITEUR)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Formulaire de Connexion Simple
 */
@Composable
private fun StandardLoginForm(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onQuickFillDemo: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                .testTag("std_login_email")
        )

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
                        contentDescription = null,
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
                .testTag("std_login_password")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onQuickFillDemo,
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFEFF6FF)
            ) {
                Text(
                    text = "Identifiants test",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Text(
                text = "Mot de passe oublié ?",
                fontSize = 12.sp,
                color = NavyDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* Reset password */ }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyDark, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_std_submit_login")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BrandAmber,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connexion en cours...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("SE CONNECTER", fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vous n'avez pas de compte ? ", fontSize = 12.sp, color = TextSecondary)
            Text(
                text = "S'inscrire",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = NavyDark,
                modifier = Modifier.clickable { onSwitchToRegister() }
            )
        }
    }
}

/**
 * Formulaire d'Inscription Standard
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StandardRegisterForm(
    name: String,
    email: String,
    phone: String,
    password: String,
    passwordVisible: Boolean,
    selectedCompanyId: String,
    selectedOperator: PaymentOperator,
    companies: List<com.example.data.models.TransportCompany>,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onCompanyChange: (String) -> Unit,
    onOperatorChange: (PaymentOperator) -> Unit,
    onSubmit: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nom et Prénoms") },
            placeholder = { Text("ex: Valence Soro") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = NavyDark)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("std_register_name")
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Adresse Email") },
            placeholder = { Text("ex: valence@gmail.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = NavyDark)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("std_register_email")
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Téléphone (Mobile Money)") },
            placeholder = { Text("+225 07 12 34 56 78") },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = NavyDark)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("std_register_phone")
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Mot de passe (min. 6 caractères)") },
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
                .testTag("std_register_password")
        )

        // Compagnie favorite
        Text(
            text = "Compagnie favorite :",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            companies.take(5).forEach { comp ->
                val isSelected = comp.id == selectedCompanyId
                Surface(
                    onClick = { onCompanyChange(comp.id) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) NavyDark else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isSelected) BrandAmber else CreamBorder)
                ) {
                    Text(
                        text = comp.shortName,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) BrandAmber else TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading && name.isNotBlank() && email.isNotBlank() && password.length >= 6,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber, contentColor = NavyDark),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_std_submit_register")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = NavyDark, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Création du compte...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("CRÉER MON COMPTE", fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vous avez déjà un compte ? ", fontSize = 12.sp, color = TextSecondary)
            Text(
                text = "Se connecter",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = NavyDark,
                modifier = Modifier.clickable { onSwitchToLogin() }
            )
        }
    }
}

/**
 * Dessin vectoriel précis du logo Google officiel "G" en 4 couleurs
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w / 2f

        // Blue right & top-right
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 315f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = size
        )
        // Green bottom
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = size
        )
        // Yellow bottom-left
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = size
        )
        // Red top-left
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(0f, 0f),
            size = size
        )

        // Center cutout
        drawCircle(
            color = Color.White,
            radius = radius * 0.58f,
            center = Offset(cx, cy)
        )

        // Middle blue bar
        val barPath = Path().apply {
            moveTo(cx, cy - radius * 0.22f)
            lineTo(w, cy - radius * 0.22f)
            lineTo(w, cy + radius * 0.22f)
            lineTo(cx, cy + radius * 0.22f)
            close()
        }
        drawPath(path = barPath, color = Color(0xFF4285F4), style = Fill)
    }
}
