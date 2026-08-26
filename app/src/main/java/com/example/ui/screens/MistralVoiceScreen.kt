package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.IvorianLanguage
import com.example.data.models.VoiceMessage
import com.example.ui.EasyTransportViewModel
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.CreamCardVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MistralVoiceScreen(
    viewModel: EasyTransportViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isRecording by viewModel.isRecordingVoice.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val messages by viewModel.voiceMessages.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("screen_mistral_voice")
    ) {
        // Top Language Selector Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = NavyDark, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Assistant Vocal Mistral IA",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Conçu pour tous les voyageurs (Lettrés & Illétrés)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavyLight
                ) {
                    Text(
                        text = "🇨🇮 Multilingue",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Language Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IvorianLanguage.values().forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setVoiceLanguage(lang) },
                        label = {
                            Text(
                                text = "${lang.label} (${lang.nativeName.split(" ").first()})",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandAmber,
                            selectedLabelColor = NavyDark,
                            containerColor = NavyLight,
                            labelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) BrandAmber else Color(0xFF475569),
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        // Quick Vocal Phrases for Illiterate or Quick Booking
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "💡 Suggestions vocales en ${selectedLanguage.label} (Appuyez pour écouter/demander) :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F)
            )
            Spacer(modifier = Modifier.height(6.dp))

            val quickPhrases = when (selectedLanguage) {
                IvorianLanguage.DIOULA -> listOf(
                    "N'bê taga Bouaké sini",
                    "STIF car be taga Korhogo joli ?",
                    "Car GPS be min sisan ?"
                )
                IvorianLanguage.BAOULE -> listOf(
                    "M'pê ticket kô Yamoussoukro",
                    "UTB car kô Bouaké",
                    "Car ô bô Autoroute su ?"
                )
                IvorianLanguage.SENOUFO -> listOf(
                    "Foufoh, mo gnin tchéli Korhogo",
                    "Car STIF kpa ticket joli ?"
                )
                IvorianLanguage.BETE -> listOf(
                    "Car San-Pédro bôkô",
                    "Billet GBTM nini joli ?"
                )
                IvorianLanguage.FRENCH -> listOf(
                    "Prochain car pour Bouaké",
                    "Où est mon car sur le GPS ?",
                    "Tarif Abidjan - Korhogo"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPhrases.forEach { phrase ->
                    Surface(
                        onClick = { viewModel.sendVoiceQuery(phrase) },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = phrase,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyDark
                            )
                        }
                    }
                }
            }
        }

        // Messages & Voice Transcripts Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                VoiceMessageItem(
                    message = msg,
                    onSpeakAgain = { viewModel.voiceAssistant.speak(msg.text) },
                    onActionClick = { action -> viewModel.handleVoiceAction(action) }
                )
            }

            if (isAiThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = BrandAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mistral IA analyse la voix...",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bottom Voice & Text Input Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 70.dp)
        ) {
            // Big Pulsing Microphone Centerpiece
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(BrandAmber.copy(alpha = 0.3f))
                    )
                }

                Surface(
                    onClick = { viewModel.toggleVoiceRecording() },
                    shape = CircleShape,
                    color = if (isRecording) Color(0xFFEF4444) else NavyDark,
                    modifier = Modifier
                        .size(62.dp)
                        .testTag("btn_mic_voice_record")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Arrêter" else "Parler",
                            tint = if (isRecording) Color.White else BrandAmber,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isRecording) "🔴 Enregistrement en cours... Parlez en ${selectedLanguage.label}" else "Appuyez sur le micro pour parler en ${selectedLanguage.label}",
                fontSize = 11.sp,
                color = if (isRecording) Color(0xFFDC2626) else TextSecondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text Fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ou écrivez votre message...", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandAmber,
                        unfocusedBorderColor = CreamBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_voice_text_fallback")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendVoiceQuery(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NavyDark)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Envoyer", tint = BrandAmber)
                }
            }
        }
    }
}

@Composable
fun VoiceMessageItem(
    message: VoiceMessage,
    onSpeakAgain: () -> Unit,
    onActionClick: (com.example.data.models.VoiceAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) NavyDark else Color.White
            ),
            border = if (!message.isUser) BorderStroke(1.dp, CreamBorder) else null,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header of the message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.isUser) "👤 Vous (${message.language.label})" else "🤖 Mistral IA (${message.language.label})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (message.isUser) BrandAmber else Color(0xFFB45309)
                    )

                    if (!message.isUser) {
                        Surface(
                            onClick = onSpeakAgain,
                            shape = CircleShape,
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Écouter",
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Écouter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (message.isUser) Color.White else TextPrimary,
                    lineHeight = 20.sp
                )

                // If this message has a generated instant action
                if (message.actionSuggested != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onActionClick(message.actionSuggested) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message.actionSuggested.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
