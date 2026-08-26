package com.example.data.models

import com.example.ui.UserRole

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String,
    val preferredOperator: PaymentOperator = PaymentOperator.WAVE,
    val favoriteCompanyId: String = "comp_cte",
    val isDemoGuest: Boolean = false,
    val loyaltyPoints: Int = 100,
    val role: UserRole = UserRole.VOYAGEUR,
    val createdAt: String = "Août 2026",
    val isVerified: Boolean = true
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSessionStarted: Boolean = false,
    val currentUser: UserProfile? = null,
    val isDemoMode: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAuthDialog: Boolean = false,
    val authTab: AuthTab = AuthTab.LOGIN
) {
    val isLoggedIn: Boolean
        get() = isSessionStarted && currentUser != null && !isDemoMode

    val isVisitorDemo: Boolean
        get() = isSessionStarted && isDemoMode

    val userDisplayName: String
        get() = currentUser?.displayName ?: if (isDemoMode) "Visiteur Démo" else "Voyageur"
}

enum class AuthTab(val title: String) {
    LOGIN("Connexion"),
    REGISTER("Inscription"),
    DEMO_VISITOR("Mode Démo (Visiteur)")
}
