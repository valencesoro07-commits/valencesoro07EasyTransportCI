package com.example.data.models

data class CinetPayConfig(
    val apiKey: String = "1298273645e7f8a9b1c2d3e4f5", // Default production-ready / test credentials
    val siteId: String = "445566",
    val notifyUrl: String = "https://easytransport.ci/api/cinetpay/notify",
    val returnUrl: String = "https://easytransport.ci/api/cinetpay/return",
    val currency: String = "XOF"
)

data class CinetPayPaymentRequest(
    val transactionId: String,
    val amount: Int,
    val currency: String = "XOF",
    val description: String,
    val customerName: String,
    val customerSurname: String,
    val customerEmail: String,
    val customerPhoneNumber: String,
    val customerAddress: String,
    val customerCity: String,
    val customerCountry: String = "CI",
    val paymentOperator: PaymentOperator
)

data class CinetPayPaymentResult(
    val isSuccess: Boolean,
    val transactionId: String,
    val message: String,
    val receiptNumber: String,
    val operatorRef: String,
    val date: String
)

enum class IvorianLanguage(val code: String, val label: String, val nativeName: String, val greetingAudio: String) {
    FRENCH("fr", "Français", "Français ivoirien", "Bonjour ! Comment puis-je vous aider à réserver votre car ?"),
    BAOULE("bci", "Baoulé", "Baoulé (Akan)", "Akwaba ! Ékro kôli n'gô ? (Bienvenue, où voulez-vous voyager ?)"),
    DIOULA("dyu", "Dioula", "Julakan / Dioula", "I ni sogoma ! I be taga min ? (Bonjour ! Où désirez-vous aller ?)"),
    SENOUFO("sef", "Sénoufo", "Sénoufo (Cebaara)", "Foufoh ! Mo gnin tchéli ? (Salutations ! Vers quelle destination partez-vous ?)"),
    BETE("bev", "Bété", "Bété (Krou)", "Dôhou ! Bôkô koua ? (Bonjour ! Quelle est votre destination ?)")
}

data class VoiceMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val language: IvorianLanguage,
    val audioDurationSeconds: Int = 3,
    val actionSuggested: VoiceAction? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class VoiceAction(
    val type: ActionType,
    val title: String,
    val payload: String
)

enum class ActionType {
    OPEN_TRIPS_SEARCH,
    TRACK_BUS_GPS,
    PAY_TICKET,
    SHOW_NEAREST_STATION,
    CALL_DISPATCH
}
