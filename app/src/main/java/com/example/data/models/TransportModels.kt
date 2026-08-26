package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class TransportCompany(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val logoRes: String,
    val rating: Float,
    val reviewCount: Int,
    val isVerified: Boolean = true,
    val fleetCount: Int,
    val phone: String,
    val headquarterCity: String,
    val description: String,
    val amenities: List<String> = listOf("Climatisation", "Wi-Fi", "Prise USB", "Suivi GPS")
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey val id: String,
    val companyId: String,
    val companyName: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureTime: String, // e.g. "07:30"
    val arrivalTime: String,   // e.g. "11:15"
    val durationText: String,  // e.g. "3h 45min"
    val price: Int,            // in FCFA (XOF)
    val busType: String,       // "VIP Climatisé", "Express Confort", "Standard"
    val busPlateNumber: String,
    val totalSeats: Int = 50,
    val availableSeats: Int = 18,
    val currentLat: Double = 5.3600, // Latitude
    val currentLng: Double = -4.0083, // Longitude
    val speedKmH: Int = 85,
    val currentStopName: String = "Gare Adjamé",
    val nextStopName: String = "Péage d'Attinguié",
    val status: TripStatus = TripStatus.SCHEDULED,
    val intermediateStops: List<String> = listOf("Péage Attinguié", "Péage Singrobo", "Toumodi"),
    val roadsidePassengersWaiting: Int = 3
)

enum class TripStatus {
    SCHEDULED,
    BOARDING,
    IN_TRANSIT,
    COMPLETED,
    DELAYED
}

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey val id: String,
    val bookingReference: String, // e.g. "ET-CI-8492"
    val tripId: String,
    val companyName: String,
    val passengerName: String,
    val passengerPhone: String,
    val passengerEmail: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureDate: String,
    val departureTime: String,
    val seatNumber: Int,
    val totalPrice: Int, // in FCFA
    val paymentOperator: PaymentOperator,
    val paymentStatus: PaymentStatus,
    val cinetPayTransactionId: String,
    val isSmsSent: Boolean = true,
    val isEmailSent: Boolean = true,
    val qrCodePayload: String,
    val isCheckedIn: Boolean = false,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val pickupType: PickupType = PickupType.STATION // STATION or ROADSIDE
)

enum class PickupType {
    STATION,
    ROADSIDE
}

enum class PaymentOperator(val displayName: String, val code: String) {
    WAVE("Wave CI", "WAVE"),
    ORANGE_MONEY("Orange Money CI", "OM"),
    MTN_MONEY("MTN MoMo", "MOMO"),
    MOOV_MONEY("Moov Money", "MOOV")
}

enum class PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED,
    PENDING
}

@Entity(tableName = "reviews")
data class CompanyReview(
    @PrimaryKey val id: String,
    val companyId: String,
    val companyName: String,
    val authorName: String,
    val rating: Int,
    val dateText: String,
    val comment: String,
    val tags: List<String> = listOf("Ponctuel", "Climatisé", "Chauffeur Prudent")
)

@Entity(tableName = "dispatch_messages")
data class DispatchMessage(
    @PrimaryKey val id: String,
    val senderName: String,
    val senderRole: String, // "Chauffeur Car #08", "Gare Adjamé", "Régulateur Toumodi"
    val message: String,
    val timestamp: String,
    val priority: MessagePriority = MessagePriority.NORMAL
)

enum class MessagePriority {
    NORMAL,
    URGENT,
    DELAY
}
