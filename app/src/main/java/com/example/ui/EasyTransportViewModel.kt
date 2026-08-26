package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.models.AuthTab
import com.example.data.models.AuthUiState
import com.example.data.models.CompanyReview
import com.example.data.models.DispatchMessage
import com.example.data.models.IvorianLanguage
import com.example.data.models.MessagePriority
import com.example.data.models.PaymentOperator
import com.example.data.models.PickupType
import com.example.data.models.Ticket
import com.example.data.models.TransportCompany
import com.example.data.models.Trip
import com.example.data.models.UserProfile
import com.example.data.models.VoiceAction
import com.example.data.models.VoiceMessage
import com.example.data.repository.CinetPayService
import com.example.data.repository.FirebaseAuthRepository
import com.example.data.repository.MistralVoiceAssistant
import com.example.data.repository.TransportRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class UserRole(val label: String, val iconName: String) {
    VOYAGEUR("Passager / Voyageur", "person"),
    CHAUFFEUR("Mode Chauffeur", "directions_bus"),
    GARE("Chef de Gare / Compagnie", "store")
}

enum class NavTab {
    HOME,
    TRIPS,
    GPS,
    TICKETS,
    VOICE,
    DRIVER_STATION
}

data class BookingUiState(
    val selectedTrip: Trip? = null,
    val selectedSeat: Int = 1,
    val passengerName: String = "Kouassi Valence",
    val passengerPhone: String = "+225 07 88 99 00 11",
    val passengerEmail: String = "valencesoro07@gmail.com",
    val selectedOperator: PaymentOperator = PaymentOperator.WAVE,
    val pickupType: PickupType = PickupType.STATION,
    val isProcessingPayment: Boolean = false,
    val lastBookedTicket: Ticket? = null,
    val paymentErrorMessage: String? = null,
    val showPaymentSheet: Boolean = false,
    val showSuccessDialog: Boolean = false
)

class EasyTransportViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val cinetPayService = CinetPayService()
    val repository = TransportRepository(db, cinetPayService)
    val voiceAssistant = MistralVoiceAssistant(application)
    private val authRepo = FirebaseAuthRepository()

    // Firebase Auth State: Starts at false to show Welcome / Inscription / Connexion / Démo screen on app launch
    private val _authState = MutableStateFlow(
        AuthUiState(
            isSessionStarted = false,
            isDemoMode = false,
            currentUser = null
        )
    )
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    // Current Mode
    private val _currentRole = MutableStateFlow(UserRole.VOYAGEUR)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Navigation Tab
    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDepartureCity = MutableStateFlow("Toutes")
    val selectedDepartureCity: StateFlow<String> = _selectedDepartureCity.asStateFlow()

    private val _selectedArrivalCity = MutableStateFlow("Toutes")
    val selectedArrivalCity: StateFlow<String> = _selectedArrivalCity.asStateFlow()

    // Data from Room
    val companies: StateFlow<List<TransportCompany>> = repository.getAllCompanies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTrips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTrips: StateFlow<List<Trip>> = combine(
        allTrips,
        _searchQuery,
        _selectedDepartureCity,
        _selectedArrivalCity
    ) { trips, query, dep, arr ->
        trips.filter { trip ->
            val matchQuery = query.isBlank() ||
                    trip.companyName.contains(query, ignoreCase = true) ||
                    trip.departureCity.contains(query, ignoreCase = true) ||
                    trip.arrivalCity.contains(query, ignoreCase = true)

            val matchDep = dep == "Toutes" || trip.departureCity.equals(dep, ignoreCase = true)
            val matchArr = arr == "Toutes" || trip.arrivalCity.equals(arr, ignoreCase = true)

            matchQuery && matchDep && matchArr
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tickets: StateFlow<List<Ticket>> = repository.getAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reviews: StateFlow<List<CompanyReview>> = repository.getAllReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dispatchMessages: StateFlow<List<DispatchMessage>> = repository.getDispatchMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Trip for GPS Live Tracking
    private val _trackedTrip = MutableStateFlow<Trip?>(null)
    val trackedTrip: StateFlow<Trip?> = _trackedTrip.asStateFlow()

    // Booking Flow State
    private val _bookingState = MutableStateFlow(BookingUiState())
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    // Selected Ticket for detail modal
    private val _selectedTicketDetail = MutableStateFlow<Ticket?>(null)
    val selectedTicketDetail: StateFlow<Ticket?> = _selectedTicketDetail.asStateFlow()

    // Mistral Voice Assistant State
    private val _selectedLanguage = MutableStateFlow(IvorianLanguage.FRENCH)
    val selectedLanguage: StateFlow<IvorianLanguage> = _selectedLanguage.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _voiceMessages = MutableStateFlow<List<VoiceMessage>>(listOf(
        VoiceMessage(
            id = "welcome_msg",
            isUser = false,
            text = "Bonjour et bienvenue sur Easy Transport CI ! Je suis votre assistant Mistral IA en langues ivoiriennes (Français, Baoulé, Dioula, Sénoufo, Bété). Parlez-moi ou appuyez sur le micro pour réserver ou suivre votre car.",
            language = IvorianLanguage.FRENCH
        )
    ))
    val voiceMessages: StateFlow<List<VoiceMessage>> = _voiceMessages.asStateFlow()

    // Driver Dashboard State
    private val _isDriverGpsBroadcasting = MutableStateFlow(true)
    val isDriverGpsBroadcasting: StateFlow<Boolean> = _isDriverGpsBroadcasting.asStateFlow()

    private val _driverOnboardCount = MutableStateFlow(38)
    val driverOnboardCount: StateFlow<Int> = _driverOnboardCount.asStateFlow()

    // User Menu & Profile Drawer State
    private val _isUserMenuOpen = MutableStateFlow(false)
    val isUserMenuOpen: StateFlow<Boolean> = _isUserMenuOpen.asStateFlow()

    private val _favoriteCompanyId = MutableStateFlow("comp_cte")
    val favoriteCompanyId: StateFlow<String> = _favoriteCompanyId.asStateFlow()

    // GPS Simulation Job
    private var gpsSimJob: Job? = null

    init {
        // Check if there is an active Firebase session
        val existingUser = authRepo.getCurrentUser()
        if (existingUser != null) {
            _authState.value = AuthUiState(
                isDemoMode = false,
                currentUser = existingUser
            )
            syncUserWithBooking(existingUser)
        }
        startGpsSimulation()
    }

    fun openAuthDialog(tab: AuthTab = AuthTab.LOGIN) {
        _authState.value = _authState.value.copy(showAuthDialog = true, authTab = tab, errorMessage = null, successMessage = null)
    }

    fun closeAuthDialog() {
        _authState.value = _authState.value.copy(showAuthDialog = false, errorMessage = null, successMessage = null)
    }

    fun clearAuthMessages() {
        _authState.value = _authState.value.copy(errorMessage = null, successMessage = null)
    }

    fun signInWithGoogle(context: android.content.Context, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = authRepo.signInWithGoogle(context)
            result.onSuccess { user ->
                _authState.value = AuthUiState(
                    isLoading = false,
                    isSessionStarted = true,
                    currentUser = user,
                    isDemoMode = false,
                    successMessage = "Connexion Google réussie ! Bienvenue ${user.displayName}",
                    showAuthDialog = false
                )
                syncUserWithBooking(user)
                onSuccess()
            }.onFailure { error ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Échec de connexion Google"
                )
            }
        }
    }

    fun signInWithFirebase(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = authRepo.signInWithEmail(email, pass)
            result.onSuccess { user ->
                _authState.value = AuthUiState(
                    isLoading = false,
                    isSessionStarted = true,
                    currentUser = user,
                    isDemoMode = false,
                    successMessage = "Connexion réussie ! Bienvenue ${user.displayName}",
                    showAuthDialog = false
                )
                syncUserWithBooking(user)
                onSuccess()
            }.onFailure { error ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Échec de connexion Firebase"
                )
            }
        }
    }

    fun registerWithFirebase(
        name: String,
        email: String,
        phone: String,
        password: String,
        companyId: String = "comp_cte",
        operator: PaymentOperator = PaymentOperator.WAVE,
        role: UserRole = UserRole.VOYAGEUR,
        onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            val result = authRepo.registerWithEmail(
                name = name,
                email = email,
                phone = phone,
                pass = password,
                favoriteCompanyId = companyId,
                operator = operator,
                role = role
            )
            result.onSuccess { user ->
                _authState.value = AuthUiState(
                    isLoading = false,
                    isSessionStarted = true,
                    currentUser = user,
                    isDemoMode = false,
                    successMessage = "Inscription réussie ! Compte Firebase activé.",
                    showAuthDialog = false
                )
                syncUserWithBooking(user)
                onSuccess()
            }.onFailure { error ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Échec d'inscription Firebase"
                )
            }
        }
    }

    fun enterDemoMode(role: UserRole = UserRole.VOYAGEUR) {
        val demoUser = authRepo.createDemoVisitorProfile(role)
        _authState.value = AuthUiState(
            isLoading = false,
            isSessionStarted = true,
            currentUser = demoUser,
            isDemoMode = true,
            successMessage = "Mode Démo Visiteur activé (${role.label})",
            showAuthDialog = false
        )
        setRole(role)
        syncUserWithBooking(demoUser)
    }

    fun signOutFromFirebase() {
        authRepo.signOut()
        _authState.value = AuthUiState(
            isSessionStarted = false,
            isDemoMode = false,
            currentUser = null,
            authTab = AuthTab.LOGIN
        )
        setRole(UserRole.VOYAGEUR)
    }

    fun returnToAuthScreen(tab: AuthTab = AuthTab.LOGIN) {
        _isUserMenuOpen.value = false
        _authState.value = _authState.value.copy(
            isSessionStarted = false,
            authTab = tab
        )
    }

    private fun syncUserWithBooking(user: UserProfile) {
        _bookingState.value = _bookingState.value.copy(
            passengerName = user.displayName,
            passengerPhone = user.phoneNumber,
            passengerEmail = user.email,
            selectedOperator = user.preferredOperator
        )
        _favoriteCompanyId.value = user.favoriteCompanyId
        _currentRole.value = user.role
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
        if (role == UserRole.CHAUFFEUR || role == UserRole.GARE) {
            _currentTab.value = NavTab.DRIVER_STATION
        }
    }

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun openUserMenu() {
        _isUserMenuOpen.value = true
    }

    fun closeUserMenu() {
        _isUserMenuOpen.value = false
    }

    fun setFavoriteCompany(companyId: String) {
        _favoriteCompanyId.value = companyId
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        email: String,
        operator: PaymentOperator
    ) {
        _bookingState.value = _bookingState.value.copy(
            passengerName = name,
            passengerPhone = phone,
            passengerEmail = email,
            selectedOperator = operator
        )
    }

    fun selectCompanyForTrips(companyId: String, companyName: String) {
        _favoriteCompanyId.value = companyId
        _searchQuery.value = companyName.substringBefore(" ").trim()
        _selectedDepartureCity.value = "Toutes"
        _selectedArrivalCity.value = "Toutes"
        _currentTab.value = NavTab.TRIPS
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun filterByRoute(dep: String, arr: String) {
        _selectedDepartureCity.value = dep
        _selectedArrivalCity.value = arr
        _currentTab.value = NavTab.TRIPS
    }

    fun setTrackedTrip(trip: Trip) {
        _trackedTrip.value = trip
        _currentTab.value = NavTab.GPS
    }

    fun startBooking(trip: Trip) {
        _bookingState.value = _bookingState.value.copy(
            selectedTrip = trip,
            selectedSeat = (1..50).random(),
            showPaymentSheet = true,
            paymentErrorMessage = null
        )
    }

    fun updateBookingForm(
        name: String = _bookingState.value.passengerName,
        phone: String = _bookingState.value.passengerPhone,
        email: String = _bookingState.value.passengerEmail,
        seat: Int = _bookingState.value.selectedSeat,
        operator: PaymentOperator = _bookingState.value.selectedOperator,
        pickupType: PickupType = _bookingState.value.pickupType
    ) {
        _bookingState.value = _bookingState.value.copy(
            passengerName = name,
            passengerPhone = phone,
            passengerEmail = email,
            selectedSeat = seat,
            selectedOperator = operator,
            pickupType = pickupType
        )
    }

    fun dismissPaymentSheet() {
        _bookingState.value = _bookingState.value.copy(showPaymentSheet = false)
    }

    fun dismissSuccessDialog() {
        _bookingState.value = _bookingState.value.copy(showSuccessDialog = false)
    }

    fun executeCinetPayPayment() {
        val trip = _bookingState.value.selectedTrip ?: return
        viewModelScope.launch {
            _bookingState.value = _bookingState.value.copy(isProcessingPayment = true, paymentErrorMessage = null)
            try {
                val (result, ticket) = repository.bookTicketWithCinetPay(
                    trip = trip,
                    passengerName = _bookingState.value.passengerName,
                    passengerPhone = _bookingState.value.passengerPhone,
                    passengerEmail = _bookingState.value.passengerEmail,
                    seatNumber = _bookingState.value.selectedSeat,
                    operator = _bookingState.value.selectedOperator,
                    pickupType = _bookingState.value.pickupType
                )
                _bookingState.value = _bookingState.value.copy(
                    isProcessingPayment = false,
                    showPaymentSheet = false,
                    lastBookedTicket = ticket,
                    showSuccessDialog = true
                )
            } catch (e: Exception) {
                _bookingState.value = _bookingState.value.copy(
                    isProcessingPayment = false,
                    paymentErrorMessage = "Échec du paiement CinetPay : ${e.localizedMessage}"
                )
            }
        }
    }

    fun setSelectedTicketDetail(ticket: Ticket?) {
        _selectedTicketDetail.value = ticket
    }

    // Mistral Voice Assistant Actions
    fun setVoiceLanguage(language: IvorianLanguage) {
        _selectedLanguage.value = language
        val greeting = VoiceMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = language.greetingAudio,
            language = language
        )
        _voiceMessages.value = _voiceMessages.value + greeting
        voiceAssistant.speak(language.greetingAudio)
    }

    fun sendVoiceQuery(query: String) {
        if (query.isBlank()) return
        val userMsg = VoiceMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = query,
            language = _selectedLanguage.value
        )
        _voiceMessages.value = _voiceMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            val botResponse = voiceAssistant.processQuery(query, _selectedLanguage.value)
            _isAiThinking.value = false
            _voiceMessages.value = _voiceMessages.value + botResponse
            voiceAssistant.speak(botResponse.text)
        }
    }

    fun toggleVoiceRecording() {
        if (_isRecordingVoice.value) {
            _isRecordingVoice.value = false
            // Simulating speech to text input based on selected language
            val sampleVoiceQuery = when (_selectedLanguage.value) {
                IvorianLanguage.DIOULA -> "N'bê taga Bouaké sini sôgôma"
                IvorianLanguage.BAOULE -> "M'pê ticket kô Yamoussoukro"
                IvorianLanguage.SENOUFO -> "Foufoh, mo gnin tchéli Korhogo"
                IvorianLanguage.BETE -> "Dôhou, car San-Pédro bôkô"
                IvorianLanguage.FRENCH -> "Je veux réserver un car UTB pour Bouaké demain matin"
            }
            sendVoiceQuery(sampleVoiceQuery)
        } else {
            _isRecordingVoice.value = true
            viewModelScope.launch {
                delay(3000)
                if (_isRecordingVoice.value) {
                    toggleVoiceRecording()
                }
            }
        }
    }

    fun handleVoiceAction(action: VoiceAction) {
        when (action.type) {
            com.example.data.models.ActionType.OPEN_TRIPS_SEARCH -> {
                if (action.payload != "ALL") {
                    _searchQuery.value = action.payload
                }
                _currentTab.value = NavTab.TRIPS
            }
            com.example.data.models.ActionType.TRACK_BUS_GPS -> {
                val trip = allTrips.value.find { it.id == action.payload } ?: allTrips.value.firstOrNull()
                trip?.let { setTrackedTrip(it) }
            }
            com.example.data.models.ActionType.PAY_TICKET -> {
                val firstTrip = allTrips.value.firstOrNull()
                firstTrip?.let { startBooking(it) }
            }
            com.example.data.models.ActionType.SHOW_NEAREST_STATION -> {
                _currentTab.value = NavTab.GPS
            }
            com.example.data.models.ActionType.CALL_DISPATCH -> {
                _currentTab.value = NavTab.DRIVER_STATION
            }
        }
    }

    fun submitReview(companyId: String, companyName: String, rating: Int, comment: String, tags: List<String>) {
        viewModelScope.launch {
            repository.addReview(
                companyId = companyId,
                companyName = companyName,
                authorName = _bookingState.value.passengerName,
                rating = rating,
                comment = comment,
                tags = tags
            )
        }
    }

    fun sendDriverDispatchMessage(message: String, priority: MessagePriority = MessagePriority.NORMAL) {
        viewModelScope.launch {
            repository.sendDispatchMessage(
                senderName = if (_currentRole.value == UserRole.CHAUFFEUR) "Chauffeur Car #08" else "Gare Adjamé Dispatch",
                senderRole = if (_currentRole.value == UserRole.CHAUFFEUR) "Chauffeur" else "Chef de Gare",
                message = message,
                priority = priority
            )
        }
    }

    fun toggleDriverGps() {
        _isDriverGpsBroadcasting.value = !_isDriverGpsBroadcasting.value
    }

    fun validatePassengerCheckIn(ticketId: String) {
        viewModelScope.launch {
            repository.validateTicketCheckIn(ticketId, true)
            _driverOnboardCount.value += 1
        }
    }

    private fun startGpsSimulation() {
        gpsSimJob?.cancel()
        gpsSimJob = viewModelScope.launch {
            // Waypoints along Autoroute du Nord (Abidjan -> Attinguié -> Singrobo -> Toumodi -> Yamoussoukro -> Bouaké)
            val waypoints = listOf(
                Pair(5.3600, -4.0083) to ("Gare Adjamé" to "Péage Attinguié"),
                Pair(5.5200, -4.2100) to ("Péage d'Attinguié" to "Péage Singrobo"),
                Pair(5.8900, -4.5600) to ("Péage de Singrobo" to "Toumodi"),
                Pair(6.2200, -4.8500) to ("Toumodi (Arrêt)" to "Yamoussoukro"),
                Pair(6.8200, -5.2767) to ("Yamoussoukro (Habitat)" to "Tiébissou"),
                Pair(7.6890, -5.0300) to ("Bouaké (Gare UTB)" to "Terminus")
            )
            var index = 2
            while (true) {
                delay(4000)
                val (coords, stops) = waypoints[index % waypoints.size]
                val speed = kotlin.random.Random.nextInt(78, 96)
                repository.updateBusLiveLocation(
                    tripId = "trip_abj_bouake_01",
                    lat = coords.first + kotlin.random.Random.nextDouble(-0.005, 0.005),
                    lng = coords.second + kotlin.random.Random.nextDouble(-0.005, 0.005),
                    speed = speed,
                    currentStop = stops.first,
                    nextStop = stops.second
                )
                index++
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistant.stopSpeaking()
        gpsSimJob?.cancel()
    }
}
