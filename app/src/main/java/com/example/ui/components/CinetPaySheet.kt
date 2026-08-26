package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaymentOperator
import com.example.data.models.PickupType
import com.example.data.models.Trip
import com.example.ui.BookingUiState
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.CreamBorder
import com.example.ui.theme.CreamCardVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MoovMoneyColor
import com.example.ui.theme.MtnMoneyColor
import com.example.ui.theme.NavyDark
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaveColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinetPaySheet(
    trip: Trip,
    bookingState: BookingUiState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSeatSelect: (Int) -> Unit,
    onOperatorSelect: (PaymentOperator) -> Unit,
    onPickupTypeSelect: (PickupType) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val formattedPrice = NumberFormat.getNumberInstance(Locale.FRENCH).format(trip.price)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Paiement Sécurisé CinetPay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                    Text(
                        text = "Mobile Money (Wave, Orange, MTN, Moov)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trip Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamCardVariant),
                border = BorderStroke(1.dp, CreamBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trip.companyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "$formattedPrice FCFA",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                    Text(
                        text = "${trip.departureCity} ➔ ${trip.arrivalCity} (${trip.departureTime})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Gare : ${trip.departureStation}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pickup Type (Station or Roadside Pickup)
            Text(
                text = "Point d'embarquement",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { onPickupTypeSelect(PickupType.STATION) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (bookingState.pickupType == PickupType.STATION) NavyDark else CreamCardVariant,
                    border = BorderStroke(1.dp, if (bookingState.pickupType == PickupType.STATION) NavyDark else CreamBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "À la Gare",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (bookingState.pickupType == PickupType.STATION) Color.White else TextPrimary
                        )
                        Text(
                            text = trip.departureStation,
                            fontSize = 10.sp,
                            color = if (bookingState.pickupType == PickupType.STATION) Color(0xFFCBD5E1) else TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    onClick = { onPickupTypeSelect(PickupType.ROADSIDE) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (bookingState.pickupType == PickupType.ROADSIDE) NavyDark else CreamCardVariant,
                    border = BorderStroke(1.dp, if (bookingState.pickupType == PickupType.ROADSIDE) NavyDark else CreamBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Sur la Route (Arrêt)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (bookingState.pickupType == PickupType.ROADSIDE) Color.White else TextPrimary
                        )
                        Text(
                            text = "Visible sur le GPS chauffeur",
                            fontSize = 10.sp,
                            color = if (bookingState.pickupType == PickupType.ROADSIDE) Color(0xFFCBD5E1) else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Passenger Info Fields
            Text(
                text = "Coordonnées du passager",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bookingState.passengerName,
                onValueChange = onNameChange,
                label = { Text("Nom et Prénoms") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandAmber) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_passenger_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandAmber,
                    unfocusedBorderColor = CreamBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bookingState.passengerPhone,
                onValueChange = onPhoneChange,
                label = { Text("Numéro Mobile Money (ex: 0788990011)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldSuccess) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_passenger_phone"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandAmber,
                    unfocusedBorderColor = CreamBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bookingState.passengerEmail,
                onValueChange = onEmailChange,
                label = { Text("Adresse Email (Réception Billet PDF)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandOrange) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_passenger_email"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandAmber,
                    unfocusedBorderColor = CreamBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Select Mobile Money Operator
            Text(
                text = "Opérateur Mobile Money (Côte d'Ivoire)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val operators = listOf(
                Pair(PaymentOperator.WAVE, WaveColor),
                Pair(PaymentOperator.ORANGE_MONEY, OrangeMoneyColor),
                Pair(PaymentOperator.MTN_MONEY, MtnMoneyColor),
                Pair(PaymentOperator.MOOV_MONEY, MoovMoneyColor)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                operators.forEach { (operator, opColor) ->
                    val isSelected = bookingState.selectedOperator == operator
                    Surface(
                        onClick = { onOperatorSelect(operator) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFF8FAFC) else Color.White,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) opColor else CreamBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("operator_select_${operator.code}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(opColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = operator.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onOperatorSelect(operator) },
                                colors = RadioButtonDefaults.colors(selectedColor = opColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CinetPay Security Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Paiement direct sécurisé par CinetPay API. Validation instantanée par SMS & Email.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pay Button
            Button(
                onClick = onConfirmPayment,
                enabled = !bookingState.isProcessingPayment && bookingState.passengerName.isNotBlank() && bookingState.passengerPhone.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandAmber,
                    disabledContainerColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_confirm_cinetpay")
            ) {
                if (bookingState.isProcessingPayment) {
                    CircularProgressIndicator(
                        color = NavyDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Traitement CinetPay...",
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        fontSize = 15.sp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PAYER $formattedPrice FCFA",
                            fontWeight = FontWeight.Black,
                            color = NavyDark,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
