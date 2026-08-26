package com.example.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.data.models.ActionType
import com.example.data.models.IvorianLanguage
import com.example.data.models.VoiceAction
import com.example.data.models.VoiceMessage
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.UUID

class MistralVoiceAssistant(context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.FRENCH
                isTtsReady = true
            }
        }
    }

    fun speak(text: String) {
        if (isTtsReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID_" + System.currentTimeMillis())
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    /**
     * Processes user speech query with Mistral AI intelligence specialized for Ivorian geography & languages.
     */
    suspend fun processQuery(
        userInput: String,
        language: IvorianLanguage
    ): VoiceMessage {
        delay(1200) // Mistral AI inference simulation

        val cleaned = userInput.lowercase(Locale.ROOT).trim()

        val (responseText, action) = when (language) {
            IvorianLanguage.DIOULA -> processDioulaQuery(cleaned)
            IvorianLanguage.BAOULE -> processBaouleQuery(cleaned)
            IvorianLanguage.SENOUFO -> processSenoufoQuery(cleaned)
            IvorianLanguage.BETE -> processBeteQuery(cleaned)
            IvorianLanguage.FRENCH -> processFrenchQuery(cleaned)
        }

        return VoiceMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = responseText,
            language = language,
            audioDurationSeconds = 4,
            actionSuggested = action
        )
    }

    private fun processDioulaQuery(query: String): Pair<String, VoiceAction?> {
        return when {
            query.contains("bouaké") || query.contains("bouake") || query.contains("taga") -> {
                Pair(
                    "A bè bô ! UTB Express kura be taga Bouaké 07h30 la, ticket ye 6 000 FCFA ye. I b'a fê ka ticket san sisan wa ?",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Voir les cars pour Bouaké", "Bouaké")
                )
            }
            query.contains("korhogo") -> {
                Pair(
                    "STIF Voyages car be taga Korhogo 08h00 la, ticket ye 10 000 FCFA ye. Ka san CinetPay / Wave fê.",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Cars pour Korhogo (10 000 F)", "Korhogo")
                )
            }
            query.contains("gps") || query.contains("min") || query.contains("car") -> {
                Pair(
                    "I ka car be Autoroute du Nord kan, a ye Toumodi géré. Suivi GPS be yen.",
                    VoiceAction(ActionType.TRACK_BUS_GPS, "Afficher la position du car sur la carte", "trip_abj_bouake_01")
                )
            }
            else -> {
                Pair(
                    "I ni tché ! Easy Transport CI bè yan ka i dèmè. I bè taga dugu juman na ? (Bouaké, Yamoussoukro, Korhogo, San Pedro ?)",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Choisir ma destination", "ALL")
                )
            }
        }
    }

    private fun processBaouleQuery(query: String): Pair<String, VoiceAction?> {
        return when {
            query.contains("bouake") || query.contains("bouaké") || query.contains("kô") -> {
                Pair(
                    "Ékro ! UTB car ô tchan kô Bouaké 07:30 su, ticket ti 6 000 FCFA. É kô fê kô fa ticket nian ?",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Nian car Bouaké (6 000 F)", "Bouaké")
                )
            }
            query.contains("yamoussoukro") || query.contains("yakro") -> {
                Pair(
                    "UTB Express ô kô Yamoussoukro 06:30 su. Ticket ti 4 000 FCFA. CinetPay Wave o kpiê su nian.",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Trajets Yamoussoukro (4 000 F)", "Yamoussoukro")
                )
            }
            query.contains("car") || query.contains("gps") -> {
                Pair(
                    "Car ô bô Autoroute su, ô dju Toumodi sisan. Carte GPS su nian.",
                    VoiceAction(ActionType.TRACK_BUS_GPS, "Voir position en direct", "trip_abj_bouake_01")
                )
            }
            else -> {
                Pair(
                    "Akwaba ! Easy Transport su, é kôli n'gô ? (Bouaké, Yamoussoukro, Korhogo ?)",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Choisir une destination", "ALL")
                )
            }
        }
    }

    private fun processSenoufoQuery(query: String): Pair<String, VoiceAction?> {
        return Pair(
            "Foufoh ! STIF Voyages tchéli Korhogo 08h00 kpa. Ticket ti 10 000 FCFA. Mo gnin ticket sisan ?",
            VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Rechercher trajet Korhogo", "Korhogo")
        )
    }

    private fun processBeteQuery(query: String): Pair<String, VoiceAction?> {
        return Pair(
            "Dôhou ! Car GBTM nini San-Pédro 07h00. Ticket ti 8 000 FCFA. Bôkô koua ?",
            VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Voir cars San-Pédro", "San-Pédro")
        )
    }

    private fun processFrenchQuery(query: String): Pair<String, VoiceAction?> {
        return when {
            query.contains("bouaké") || query.contains("bouake") -> {
                Pair(
                    "Le prochain départ pour Bouaké est à 07h30 avec UTB Express (VIP Climatisé). Tarif : 6 000 FCFA. Il reste 8 places disponibles.",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Réserver Abidjan - Bouaké (6 000 FCFA)", "Bouaké")
                )
            }
            query.contains("yamoussoukro") || query.contains("yakro") -> {
                Pair(
                    "UTB Express propose un départ vers Yamoussoukro à 06h30 au tarif de 4 000 FCFA (2h45 de route via l'Autoroute du Nord).",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Réserver Abidjan - Yamoussoukro", "Yamoussoukro")
                )
            }
            query.contains("korhogo") -> {
                Pair(
                    "STIF Voyages assure la liaison vers Korhogo au départ d'Adjamé à 08h00 pour 10 000 FCFA. Billet disponible immédiatement par Mobile Money.",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Réserver Abidjan - Korhogo", "Korhogo")
                )
            }
            query.contains("gps") || query.contains("position") || query.contains("où est") -> {
                Pair(
                    "Le car UTB #08 est actuellement géolocalisé près de Toumodi, circulant à 88 km/h. Arrivée prévue à Bouaké dans environ 1 heure.",
                    VoiceAction(ActionType.TRACK_BUS_GPS, "Ouvrir la carte de suivi GPS en direct", "trip_abj_bouake_01")
                )
            }
            query.contains("payer") || query.contains("cinetpay") || query.contains("wave") || query.contains("orange") -> {
                Pair(
                    "Easy Transport CI accepte Wave, Orange Money, MTN MoMo et Moov Money via la passerelle sécurisée CinetPay avec réception instantanée du ticket par SMS et QR Code.",
                    VoiceAction(ActionType.PAY_TICKET, "Paiement Sécurisé CinetPay", "CinetPay")
                )
            }
            query.contains("gare") || query.contains("adjamé") || query.contains("yopougon") -> {
                Pair(
                    "Les principales gares partenaires sont : Gare UTB Adjamé, Gare STIF Adjamé Renault, Gare GBTM Siporex Yopougon, et Gare Express Mondial Treichville.",
                    VoiceAction(ActionType.SHOW_NEAREST_STATION, "Voir les gares sur la carte", "STATIONS")
                )
            }
            else -> {
                Pair(
                    "Bonjour ! Je suis l'assistant vocal Easy Transport CI. Vous pouvez me demander un horaire, un tarif de car, suivre la position GPS de votre véhicule ou réserver un billet par la voix en Français, Baoulé, Dioula, Sénoufo ou Bété.",
                    VoiceAction(ActionType.OPEN_TRIPS_SEARCH, "Rechercher un voyage", "ALL")
                )
            }
        }
    }
}
