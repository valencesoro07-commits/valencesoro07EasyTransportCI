package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.CompanyReview
import com.example.data.models.DispatchMessage
import com.example.data.models.MessagePriority
import com.example.data.models.PaymentOperator
import com.example.data.models.PaymentStatus
import com.example.data.models.PickupType
import com.example.data.models.Ticket
import com.example.data.models.TransportCompany
import com.example.data.models.Trip
import com.example.data.models.TripStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) emptyList() else value.split("||")
    }

    @TypeConverter
    fun fromTripStatus(status: TripStatus?): String = status?.name ?: TripStatus.SCHEDULED.name

    @TypeConverter
    fun toTripStatus(value: String?): TripStatus =
        value?.let { runCatching { TripStatus.valueOf(it) }.getOrNull() } ?: TripStatus.SCHEDULED

    @TypeConverter
    fun fromPaymentOperator(operator: PaymentOperator?): String = operator?.name ?: PaymentOperator.WAVE.name

    @TypeConverter
    fun toPaymentOperator(value: String?): PaymentOperator =
        value?.let { runCatching { PaymentOperator.valueOf(it) }.getOrNull() } ?: PaymentOperator.WAVE

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus?): String = status?.name ?: PaymentStatus.SUCCESS.name

    @TypeConverter
    fun toPaymentStatus(value: String?): PaymentStatus =
        value?.let { runCatching { PaymentStatus.valueOf(it) }.getOrNull() } ?: PaymentStatus.SUCCESS

    @TypeConverter
    fun fromPickupType(pickup: PickupType?): String = pickup?.name ?: PickupType.STATION.name

    @TypeConverter
    fun toPickupType(value: String?): PickupType =
        value?.let { runCatching { PickupType.valueOf(it) }.getOrNull() } ?: PickupType.STATION

    @TypeConverter
    fun fromMessagePriority(priority: MessagePriority?): String = priority?.name ?: MessagePriority.NORMAL.name

    @TypeConverter
    fun toMessagePriority(value: String?): MessagePriority =
        value?.let { runCatching { MessagePriority.valueOf(it) }.getOrNull() } ?: MessagePriority.NORMAL
}

@Database(
    entities = [
        TransportCompany::class,
        Trip::class,
        Ticket::class,
        CompanyReview::class,
        DispatchMessage::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): TransportCompanyDao
    abstract fun tripDao(): TripDao
    abstract fun ticketDao(): TicketDao
    abstract fun reviewDao(): ReviewDao
    abstract fun dispatchMessageDao(): DispatchMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "easy_transport_ci.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateInitialData(getDatabase(context))
            }
        }
    }
}

suspend fun populateInitialData(db: AppDatabase) {
    val companies = listOf(
        TransportCompany(
            id = "comp_cte",
            name = "CTE Transport (Compagnie de Transport Express)",
            shortName = "CTE",
            logoRes = "cte",
            rating = 4.7f,
            reviewCount = 980,
            isVerified = true,
            fleetCount = 55,
            phone = "+225 27 20 38 15 15",
            headquarterCity = "Abidjan / Man",
            description = "Spécialiste incontournable du Grand Ouest (Man, Danané, Duékoué, Touba, Guiglo) et du Centre-Ouest. Confort VIP et chauffeurs de haute montagne certifiés.",
            amenities = listOf("Climatisation Intégrale", "Wi-Fi à bord", "Prises USB", "Suivi GPS 24/7", "Écran HD")
        ),
        TransportCompany(
            id = "comp_utb",
            name = "UTB Express (Union des Transports de Bouaké)",
            shortName = "UTB",
            logoRes = "utb",
            rating = 4.8f,
            reviewCount = 1420,
            isVerified = true,
            fleetCount = 120,
            phone = "+225 27 20 37 44 44",
            headquarterCity = "Bouaké / Abidjan",
            description = "Leader du transport interurbain en Côte d'Ivoire. Flotte moderne climatisée desservant tout le réseau national.",
            amenities = listOf("Climatisation", "Wi-Fi Haut Débit", "Prises USB", "Suivi GPS 24/7", "Écran TV")
        ),
        TransportCompany(
            id = "comp_stif",
            name = "STIF Voyages",
            shortName = "STIF",
            logoRes = "stif",
            rating = 4.6f,
            reviewCount = 890,
            isVerified = true,
            fleetCount = 65,
            phone = "+225 27 21 24 55 10",
            headquarterCity = "Abidjan (Adjamé)",
            description = "Confort et ponctualité sur l'axe Abidjan - Yamoussoukro - Bouaké - Korhogo.",
            amenities = listOf("Climatisation", "Sièges inclinables", "Suivi GPS", "Bagages sécurisés")
        ),
        TransportCompany(
            id = "comp_avs",
            name = "AVS Voyages (Avenir Vallée du Sassandra)",
            shortName = "AVS",
            logoRes = "avs",
            rating = 4.5f,
            reviewCount = 540,
            isVerified = true,
            fleetCount = 42,
            phone = "+225 27 32 78 12 90",
            headquarterCity = "Abidjan / Daloa / Vavoua",
            description = "Liaisons directes rapides vers le Haut-Sassandra (Daloa, Vavoua, Zuénoula, Issia).",
            amenities = listOf("Climatisation", "Sièges VIP", "Suivi GPS", "Chauffeurs Prudence")
        ),
        TransportCompany(
            id = "comp_sitraf",
            name = "SITRAF Express",
            shortName = "SITRAF",
            logoRes = "sitraf",
            rating = 4.4f,
            reviewCount = 410,
            isVerified = true,
            fleetCount = 35,
            phone = "+225 27 35 91 00 22",
            headquarterCity = "Abidjan / Abengourou / Bondoukou",
            description = "Le grand transporteur de l'Indénié-Djuablin et du Gontougo (Bondoukou, Bouna).",
            amenities = listOf("Climatisation", "Ponctualité", "Suivi GPS 24/7")
        ),
        TransportCompany(
            id = "comp_gks",
            name = "GKS Transport",
            shortName = "GKS",
            logoRes = "gks",
            rating = 4.5f,
            reviewCount = 360,
            isVerified = true,
            fleetCount = 32,
            phone = "+225 07 48 59 60 71",
            headquarterCity = "Abidjan / Katiola / Korhogo",
            description = "Spécialiste de la route du Nord, Katiola, Niakaramandougou et Korhogo.",
            amenities = listOf("Climatisation", "Vidéo embarquée", "Arrêt buffet")
        ),
        TransportCompany(
            id = "comp_express_mondial",
            name = "Express Mondial",
            shortName = "Express Mondial",
            logoRes = "mondial",
            rating = 4.5f,
            reviewCount = 612,
            isVerified = true,
            fleetCount = 45,
            phone = "+225 07 08 09 10 11",
            headquarterCity = "Abidjan (Treichville)",
            description = "Liaisons rapides et sécurisées vers le Sud-Ouest et le Centre.",
            amenities = listOf("Climatisation", "Ponctualité garantie", "Suivi GPS")
        ),
        TransportCompany(
            id = "comp_gbtm",
            name = "GBTM Tourisme & Transport",
            shortName = "GBTM",
            logoRes = "gbtm",
            rating = 4.4f,
            reviewCount = 430,
            isVerified = true,
            fleetCount = 38,
            phone = "+225 27 22 41 80 00",
            headquarterCity = "Abidjan / San-Pédro",
            description = "Spécialiste de la côtière et des liaisons Abidjan - San Pedro - Soubré.",
            amenities = listOf("Climatisation", "Vidéo à bord", "Arrêts rapides")
        ),
        TransportCompany(
            id = "comp_sbta",
            name = "SBTA Express",
            shortName = "SBTA",
            logoRes = "sbta",
            rating = 4.3f,
            reviewCount = 280,
            isVerified = true,
            fleetCount = 28,
            phone = "+225 27 24 38 12 00",
            headquarterCity = "Abidjan / Gagnoa",
            description = "Réseau historique du Gôh et du Gbôklè.",
            amenities = listOf("Confort", "Sécurité", "Prix abordables")
        ),
        TransportCompany(
            id = "comp_mt_inter",
            name = "MT International (Malex Transport)",
            shortName = "MT Inter",
            logoRes = "mt",
            rating = 4.4f,
            reviewCount = 295,
            isVerified = true,
            fleetCount = 25,
            phone = "+225 05 77 88 99 00",
            headquarterCity = "Abidjan / San-Pédro / Man",
            description = "Connexions transversales sécurisées entre l'Ouest et le littoral Sud-Ouest.",
            amenities = listOf("Climatisation", "Sièges grand confort", "Suivi GPS")
        )
    )
    db.companyDao().insertCompanies(companies)

    val trips = listOf(
        Trip(
            id = "trip_abj_yakro_01",
            companyId = "comp_utb",
            companyName = "UTB Express",
            departureCity = "Abidjan",
            arrivalCity = "Yamoussoukro",
            departureStation = "Gare UTB Adjamé",
            arrivalStation = "Gare UTB Yamoussoukro (Habitat)",
            departureTime = "06:30",
            arrivalTime = "09:15",
            durationText = "2h 45min",
            price = 4000,
            busType = "VIP Climatisé",
            busPlateNumber = "CI-8924-HH01",
            totalSeats = 50,
            availableSeats = 14,
            currentLat = 5.6800,
            currentLng = -4.3800,
            speedKmH = 92,
            currentStopName = "Péage d'Attinguié",
            nextStopName = "Péage de Singrobo",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 2
        ),
        Trip(
            id = "trip_abj_bouake_01",
            companyId = "comp_utb",
            companyName = "UTB Express",
            departureCity = "Abidjan",
            arrivalCity = "Bouaké",
            departureStation = "Gare UTB Adjamé",
            arrivalStation = "Gare UTB Bouaké (Commerce)",
            departureTime = "07:30",
            arrivalTime = "11:45",
            durationText = "4h 15min",
            price = 6000,
            busType = "VIP Climatisé",
            busPlateNumber = "CI-4512-JK01",
            totalSeats = 52,
            availableSeats = 8,
            currentLat = 6.2200,
            currentLng = -4.8500,
            speedKmH = 88,
            currentStopName = "Toumodi (Arrêt Carrefour)",
            nextStopName = "Gare Yamoussoukro",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 4
        ),
        Trip(
            id = "trip_abj_korhogo_01",
            companyId = "comp_stif",
            companyName = "STIF Voyages",
            departureCity = "Abidjan",
            arrivalCity = "Korhogo",
            departureStation = "Gare STIF Adjamé Renault",
            arrivalStation = "Gare STIF Korhogo (Koko)",
            departureTime = "08:00",
            arrivalTime = "16:30",
            durationText = "8h 30min",
            price = 10000,
            busType = "Confort Plus Climatisé",
            busPlateNumber = "CI-9988-MM01",
            totalSeats = 54,
            availableSeats = 19,
            currentLat = 5.3850,
            currentLng = -4.0200,
            speedKmH = 0,
            currentStopName = "Gare Adjamé (Embarquement)",
            nextStopName = "Péage Attinguié",
            status = TripStatus.BOARDING,
            roadsidePassengersWaiting = 5
        ),
        Trip(
            id = "trip_abj_sanpedro_01",
            companyId = "comp_gbtm",
            companyName = "GBTM Tourisme",
            departureCity = "Abidjan",
            arrivalCity = "San-Pédro",
            departureStation = "Gare Yopougon Siporex",
            arrivalStation = "Gare Routière San-Pédro",
            departureTime = "07:00",
            arrivalTime = "12:30",
            durationText = "5h 30min",
            price = 8000,
            busType = "Express Climatisé",
            busPlateNumber = "CI-3341-LL01",
            totalSeats = 48,
            availableSeats = 12,
            currentLat = 5.2500,
            currentLng = -4.8000,
            speedKmH = 75,
            currentStopName = "Dabou (Carrefour)",
            nextStopName = "Grand-Lahou",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 3
        ),
        Trip(
            id = "trip_abj_daloa_01",
            companyId = "comp_express_mondial",
            companyName = "Express Mondial",
            departureCity = "Abidjan",
            arrivalCity = "Daloa",
            departureStation = "Gare Treichville Belleville",
            arrivalStation = "Gare Daloa Lobia",
            departureTime = "09:00",
            arrivalTime = "14:30",
            durationText = "5h 30min",
            price = 7000,
            busType = "Standard Confort",
            busPlateNumber = "CI-7762-NN01",
            totalSeats = 50,
            availableSeats = 25,
            currentLat = 5.3400,
            currentLng = -4.0100,
            speedKmH = 0,
            currentStopName = "Gare Treichville",
            nextStopName = "Péage Attinguié",
            status = TripStatus.SCHEDULED,
            roadsidePassengersWaiting = 1
        ),
        Trip(
            id = "trip_abj_man_01",
            companyId = "comp_cte",
            companyName = "CTE Transport",
            departureCity = "Abidjan",
            arrivalCity = "Man",
            departureStation = "Gare Adjamé Mirador",
            arrivalStation = "Gare Centrale de Man",
            departureTime = "06:00",
            arrivalTime = "15:00",
            durationText = "9h 00min",
            price = 11000,
            busType = "VIP 18 Montagnes Climatisé",
            busPlateNumber = "CI-1122-PP01",
            totalSeats = 50,
            availableSeats = 6,
            currentLat = 6.8900,
            currentLng = -5.7800,
            speedKmH = 80,
            currentStopName = "Yamoussoukro Carrefour",
            nextStopName = "Daloa",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 4
        ),
        Trip(
            id = "trip_abj_danane_01",
            companyId = "comp_cte",
            companyName = "CTE Transport",
            departureCity = "Abidjan",
            arrivalCity = "Danané",
            departureStation = "Gare Adjamé Mirador",
            arrivalStation = "Gare CTE Danané Ville",
            departureTime = "06:30",
            arrivalTime = "16:30",
            durationText = "10h 00min",
            price = 12500,
            busType = "Grand Confort Climatisé",
            busPlateNumber = "CI-3390-PP01",
            totalSeats = 50,
            availableSeats = 15,
            currentLat = 5.3500,
            currentLng = -4.0300,
            speedKmH = 0,
            currentStopName = "Gare Adjamé Mirador",
            nextStopName = "Péage Attinguié",
            status = TripStatus.BOARDING,
            roadsidePassengersWaiting = 3
        ),
        Trip(
            id = "trip_abj_vavoua_01",
            companyId = "comp_avs",
            companyName = "AVS Voyages",
            departureCity = "Abidjan",
            arrivalCity = "Vavoua",
            departureStation = "Gare Adjamé Liberté",
            arrivalStation = "Gare AVS Vavoua Centre",
            departureTime = "07:15",
            arrivalTime = "13:30",
            durationText = "6h 15min",
            price = 8500,
            busType = "VIP Vallée Sassandra",
            busPlateNumber = "CI-8812-QQ01",
            totalSeats = 48,
            availableSeats = 11,
            currentLat = 6.4500,
            currentLng = -5.3000,
            speedKmH = 85,
            currentStopName = "Zuénoula",
            nextStopName = "Daloa Carrefour",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 2
        ),
        Trip(
            id = "trip_abj_bondoukou_01",
            companyId = "comp_sitraf",
            companyName = "SITRAF Express",
            departureCity = "Abidjan",
            arrivalCity = "Bondoukou",
            departureStation = "Gare Adjamé Dallas",
            arrivalStation = "Gare Centrale Bondoukou",
            departureTime = "06:00",
            arrivalTime = "13:00",
            durationText = "7h 00min",
            price = 9000,
            busType = "Climatisé Gontougo Express",
            busPlateNumber = "CI-4455-RR01",
            totalSeats = 50,
            availableSeats = 18,
            currentLat = 6.7300,
            currentLng = -3.4900,
            speedKmH = 82,
            currentStopName = "Abengourou",
            nextStopName = "Agnibilékrou",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 1
        ),
        Trip(
            id = "trip_abj_gagnoa_01",
            companyId = "comp_sbta",
            companyName = "SBTA Express",
            departureCity = "Abidjan",
            arrivalCity = "Gagnoa",
            departureStation = "Gare Treichville Gare de Bassam",
            arrivalStation = "Gare SBTA Gagnoa Dioulabougou",
            departureTime = "08:30",
            arrivalTime = "13:00",
            durationText = "4h 30min",
            price = 5500,
            busType = "Standard Confort",
            busPlateNumber = "CI-6622-SS01",
            totalSeats = 52,
            availableSeats = 22,
            currentLat = 5.8300,
            currentLng = -5.3500,
            speedKmH = 78,
            currentStopName = "Divo (Arrêt Péage)",
            nextStopName = "Gagnoa",
            status = TripStatus.IN_TRANSIT,
            roadsidePassengersWaiting = 3
        )
    )
    db.tripDao().insertTrips(trips)

    val sampleTickets = listOf(
        Ticket(
            id = "tkt_sample_01",
            bookingReference = "ET-CI-84920",
            tripId = "trip_abj_bouake_01",
            companyName = "UTB Express",
            passengerName = "Kouassi Valence",
            passengerPhone = "+225 07 88 99 00 11",
            passengerEmail = "valencesoro07@gmail.com",
            departureCity = "Abidjan",
            arrivalCity = "Bouaké",
            departureStation = "Gare UTB Adjamé",
            arrivalStation = "Gare UTB Bouaké (Commerce)",
            departureDate = "Aujourd'hui, 25 Août",
            departureTime = "07:30",
            seatNumber = 14,
            totalPrice = 6000,
            paymentOperator = PaymentOperator.WAVE,
            paymentStatus = PaymentStatus.SUCCESS,
            cinetPayTransactionId = "CPAY_WAVE_99837482",
            isSmsSent = true,
            isEmailSent = true,
            qrCodePayload = "EASY_TRANSPORT_CI|ET-CI-84920|Kouassi Valence|UTB|Abidjan-Bouake|Seat-14|PAID_WAVE",
            isCheckedIn = true,
            pickupType = PickupType.STATION
        )
    )
    for (t in sampleTickets) {
        db.ticketDao().insertTicket(t)
    }

    val reviews = listOf(
        CompanyReview(
            id = "rev_1",
            companyId = "comp_utb",
            companyName = "UTB Express",
            authorName = "Awa Koné",
            rating = 5,
            dateText = "Il y a 2 jours",
            comment = "Superbe voyage Abidjan - Bouaké ! Le car était très propre, climatisation au top et départ à l'heure exacte. Le suivi GPS m'a permis de rassurer ma famille.",
            tags = listOf("Ponctuel", "Climatisé", "Chauffeur Prudent", "Suivi GPS")
        ),
        CompanyReview(
            id = "rev_2",
            companyId = "comp_utb",
            companyName = "UTB Express",
            authorName = "Mamadou Traoré",
            rating = 5,
            dateText = "Il y a 4 jours",
            comment = "Le paiement Wave avec CinetPay est instantané, billet reçu par SMS immédiatement avec le QR code. C'est l'avenir du transport ivoirien !",
            tags = listOf("Paiement Facile", "SMS Reçu", "Service Client")
        ),
        CompanyReview(
            id = "rev_3",
            companyId = "comp_stif",
            companyName = "STIF Voyages",
            authorName = "Yao Franck",
            rating = 4,
            dateText = "La semaine dernière",
            comment = "Bonne compagnie pour aller à Korhogo. Le chauffeur roulait bien et le personnel de la gare d'Adjamé était très accueillant.",
            tags = listOf("Confort", "Sécurité")
        )
    )
    db.reviewDao().insertReviews(reviews)

    val messages = listOf(
        DispatchMessage(
            id = "msg_1",
            senderName = "Gare Adjamé Dispatch",
            senderRole = "Chef de Gare",
            message = "Car UTB #08 (CI-4512-JK01) en route pour Bouaké. 4 passagers en attente au carrefour Toumodi avec billets Easy Transport validés.",
            timestamp = "08:15",
            priority = MessagePriority.NORMAL
        ),
        DispatchMessage(
            id = "msg_2",
            senderName = "Chauffeur Sékou (Car #08)",
            senderRole = "Chauffeur",
            message = "Bien reçu Gare Adjamé ! Nous venons de passer le péage de Singrobo, vitesse 90 km/h, climatisation OK, 44 passagers à bord.",
            timestamp = "08:42",
            priority = MessagePriority.NORMAL
        ),
        DispatchMessage(
            id = "msg_3",
            senderName = "Régulateur Autoroute",
            senderRole = "Sécurité & Trafic",
            message = "Trafic fluide sur l'Autoroute du Nord. Arrêt de 5 minutes autorisé à la station Toumodi.",
            timestamp = "09:05",
            priority = MessagePriority.NORMAL
        )
    )
    db.dispatchMessageDao().insertMessages(messages)
}
