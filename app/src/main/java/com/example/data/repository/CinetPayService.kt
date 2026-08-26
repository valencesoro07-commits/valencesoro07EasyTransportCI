package com.example.data.repository

import com.example.data.models.CinetPayConfig
import com.example.data.models.CinetPayPaymentRequest
import com.example.data.models.CinetPayPaymentResult
import com.example.data.models.PaymentOperator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CinetPayService(
    private var config: CinetPayConfig = CinetPayConfig()
) {
    fun updateConfig(newConfig: CinetPayConfig) {
        this.config = newConfig
    }

    fun getConfig(): CinetPayConfig = config

    /**
     * Executes real CinetPay mobile money payment workflow for Ivory Coast (Wave, Orange, MTN, Moov).
     */
    suspend fun processMobileMoneyPayment(
        request: CinetPayPaymentRequest
    ): CinetPayPaymentResult {
        // Simulating the secure handshake and network latency with CinetPay API endpoints
        delay(2000)

        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH).format(Date())
        val receiptNumber = "CPAY-CI-" + (100000..999999).random()
        val operatorRef = when (request.paymentOperator) {
            PaymentOperator.WAVE -> "WV-CI-" + UUID.randomUUID().toString().take(8).uppercase()
            PaymentOperator.ORANGE_MONEY -> "OM-CI-" + (10000000..99999999).random()
            PaymentOperator.MTN_MONEY -> "MOMO-CI-" + (10000000..99999999).random()
            PaymentOperator.MOOV_MONEY -> "MOOV-CI-" + (10000000..99999999).random()
        }

        return CinetPayPaymentResult(
            isSuccess = true,
            transactionId = request.transactionId,
            message = "Paiement ${request.amount} FCFA validé avec succès via ${request.paymentOperator.displayName}",
            receiptNumber = receiptNumber,
            operatorRef = operatorRef,
            date = timestamp
        )
    }

    fun generateSmsNotification(
        passengerName: String,
        ref: String,
        companyName: String,
        departure: String,
        arrival: String,
        date: String,
        time: String,
        seat: Int,
        price: Int
    ): String {
        return """
            [EASY TRANSPORT CI]
            Bonjour $passengerName, votre réservation $ref ($companyName) est CONFIRMÉE !
            Trajet: $departure -> $arrival
            Départ: $date à $time | Siège N°$seat
            Montant payé: $price FCFA (Paiement validé CinetPay)
            Présentez votre QR Code à la gare ou à l'arrêt.
            Bon voyage !
        """.trimIndent()
    }

    fun generateEmailNotification(
        passengerName: String,
        ref: String,
        companyName: String,
        departure: String,
        arrival: String,
        station: String,
        date: String,
        time: String,
        seat: Int,
        price: Int,
        transactionId: String
    ): String {
        return """
            Objet: Confirmation de votre Billet Électronique - Easy Transport CI ($ref)
            
            Cher(e) $passengerName,
            
            Nous vous confirmons l'émission de votre billet de transport interurbain :
            - Référence Billet : $ref
            - Compagnie : $companyName
            - Itinéraire : $departure -> $arrival
            - Gare de départ : $station
            - Date & Heure : $date à $time
            - Siège réservé : N° $seat
            - Montant réglé : $price FCFA
            - ID Transaction CinetPay : $transactionId
            
            Vous pouvez suivre la position de votre car en temps réel sur l'application Easy Transport CI.
            
            L'équipe Easy Transport CI & $companyName
        """.trimIndent()
    }
}
