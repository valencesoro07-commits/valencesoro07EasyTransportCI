package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.models.CinetPayPaymentRequest
import com.example.data.models.CinetPayPaymentResult
import com.example.data.models.CompanyReview
import com.example.data.models.DispatchMessage
import com.example.data.models.MessagePriority
import com.example.data.models.PaymentOperator
import com.example.data.models.PaymentStatus
import com.example.data.models.PickupType
import com.example.data.models.Ticket
import com.example.data.models.TransportCompany
import com.example.data.models.Trip
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TransportRepository(
    private val database: AppDatabase,
    private val cinetPayService: CinetPayService
) {
    fun getAllCompanies(): Flow<List<TransportCompany>> = database.companyDao().getAllCompanies()

    fun getAllTrips(): Flow<List<Trip>> = database.tripDao().getAllTrips()

    fun searchTrips(departure: String, arrival: String): Flow<List<Trip>> =
        database.tripDao().searchTrips(departure, arrival)

    suspend fun getTripById(tripId: String): Trip? = database.tripDao().getTripById(tripId)

    fun getAllTickets(): Flow<List<Ticket>> = database.ticketDao().getAllTickets()

    fun getAllReviews(): Flow<List<CompanyReview>> = database.reviewDao().getAllReviews()

    fun getReviewsForCompany(companyId: String): Flow<List<CompanyReview>> =
        database.reviewDao().getReviewsForCompany(companyId)

    fun getDispatchMessages(): Flow<List<DispatchMessage>> = database.dispatchMessageDao().getMessages()

    suspend fun addReview(
        companyId: String,
        companyName: String,
        authorName: String,
        rating: Int,
        comment: String,
        tags: List<String>
    ) {
        val review = CompanyReview(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            companyId = companyId,
            companyName = companyName,
            authorName = authorName,
            rating = rating,
            dateText = "Aujourd'hui",
            comment = comment,
            tags = tags
        )
        database.reviewDao().insertReview(review)
    }

    suspend fun sendDispatchMessage(
        senderName: String,
        senderRole: String,
        message: String,
        priority: MessagePriority = MessagePriority.NORMAL
    ) {
        val time = SimpleDateFormat("HH:mm", Locale.FRENCH).format(Date())
        val dispatchMessage = DispatchMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            senderName = senderName,
            senderRole = senderRole,
            message = message,
            timestamp = time,
            priority = priority
        )
        database.dispatchMessageDao().insertMessage(dispatchMessage)
    }

    suspend fun validateTicketCheckIn(ticketId: String, isValidated: Boolean = true) {
        database.ticketDao().updateCheckInStatus(ticketId, isValidated)
    }

    /**
     * Complete Booking with real CinetPay Mobile Money checkout and Ticket Generation
     */
    suspend fun bookTicketWithCinetPay(
        trip: Trip,
        passengerName: String,
        passengerPhone: String,
        passengerEmail: String,
        seatNumber: Int,
        operator: PaymentOperator,
        pickupType: PickupType = PickupType.STATION
    ): Pair<CinetPayPaymentResult, Ticket> {
        val transactionId = "CPAY_TX_" + (10000000..99999999).random()
        val bookingRef = "ET-CI-" + (10000..99999).random()

        val request = CinetPayPaymentRequest(
            transactionId = transactionId,
            amount = trip.price,
            description = "Billet EasyTransport CI ${trip.companyName} (${trip.departureCity} - ${trip.arrivalCity})",
            customerName = passengerName.split(" ").firstOrNull() ?: "Passager",
            customerSurname = passengerName.split(" ").getOrNull(1) ?: "CI",
            customerEmail = passengerEmail.ifBlank { "client@easytransport.ci" },
            customerPhoneNumber = passengerPhone,
            customerAddress = trip.departureCity,
            customerCity = trip.departureCity,
            paymentOperator = operator
        )

        val paymentResult = cinetPayService.processMobileMoneyPayment(request)

        val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(Date())
        val qrPayload = "EASY_TRANSPORT_CI|$bookingRef|$passengerName|${trip.companyName}|${trip.departureCity}-${trip.arrivalCity}|Seat-$seatNumber|${operator.name}|$transactionId"

        val ticket = Ticket(
            id = "tkt_" + UUID.randomUUID().toString(),
            bookingReference = bookingRef,
            tripId = trip.id,
            companyName = trip.companyName,
            passengerName = passengerName,
            passengerPhone = passengerPhone,
            passengerEmail = passengerEmail,
            departureCity = trip.departureCity,
            arrivalCity = trip.arrivalCity,
            departureStation = trip.departureStation,
            arrivalStation = trip.arrivalStation,
            departureDate = dateStr,
            departureTime = trip.departureTime,
            seatNumber = seatNumber,
            totalPrice = trip.price,
            paymentOperator = operator,
            paymentStatus = if (paymentResult.isSuccess) PaymentStatus.SUCCESS else PaymentStatus.FAILED,
            cinetPayTransactionId = transactionId,
            isSmsSent = true,
            isEmailSent = true,
            qrCodePayload = qrPayload,
            isCheckedIn = false,
            pickupType = pickupType
        )

        database.ticketDao().insertTicket(ticket)

        // Decrement available seats
        val newAvailable = (trip.availableSeats - 1).coerceAtLeast(0)
        database.tripDao().updateSeats(trip.id, newAvailable)

        return Pair(paymentResult, ticket)
    }

    suspend fun updateBusLiveLocation(
        tripId: String,
        lat: Double,
        lng: Double,
        speed: Int,
        currentStop: String,
        nextStop: String
    ) {
        database.tripDao().updateTripGps(tripId, lat, lng, speed, currentStop, nextStop)
    }

    fun getCinetPayConfig() = cinetPayService.getConfig()
    fun updateCinetPayConfig(config: com.example.data.models.CinetPayConfig) = cinetPayService.updateConfig(config)
}
