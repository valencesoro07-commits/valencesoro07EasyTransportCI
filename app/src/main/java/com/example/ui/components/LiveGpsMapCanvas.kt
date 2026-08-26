package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.models.Trip
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySurface

data class MapStation(
    val name: String,
    val normX: Float, // 0.0 to 1.0
    val normY: Float, // 0.0 to 1.0 (0=North, 1=South)
    val type: StationType = StationType.MAJOR_CITY
)

enum class StationType {
    MAJOR_CITY,
    TOLL_BOOTH,
    HIGHWAY_STOP
}

val IVORY_COAST_MAP_STATIONS = listOf(
    MapStation("Korhogo (Nord)", 0.48f, 0.12f, StationType.MAJOR_CITY),
    MapStation("Katiola", 0.50f, 0.28f, StationType.HIGHWAY_STOP),
    MapStation("Bouaké (Centre)", 0.52f, 0.38f, StationType.MAJOR_CITY),
    MapStation("Tiébissou", 0.51f, 0.48f, StationType.HIGHWAY_STOP),
    MapStation("Yamoussoukro", 0.48f, 0.56f, StationType.MAJOR_CITY),
    MapStation("Toumodi (Arrêt)", 0.52f, 0.65f, StationType.HIGHWAY_STOP),
    MapStation("Péage Singrobo", 0.54f, 0.73f, StationType.TOLL_BOOTH),
    MapStation("Péage Attinguié", 0.56f, 0.81f, StationType.TOLL_BOOTH),
    MapStation("Abidjan (Adjamé)", 0.60f, 0.89f, StationType.MAJOR_CITY),
    MapStation("Dabou", 0.42f, 0.89f, StationType.HIGHWAY_STOP),
    MapStation("Grand-Lahou", 0.32f, 0.91f, StationType.HIGHWAY_STOP),
    MapStation("San-Pédro (Côtière)", 0.18f, 0.92f, StationType.MAJOR_CITY),
    MapStation("Daloa (Centre-Ouest)", 0.32f, 0.58f, StationType.MAJOR_CITY),
    MapStation("Man (18 Montagnes)", 0.18f, 0.48f, StationType.MAJOR_CITY)
)

@Composable
fun LiveGpsMapCanvas(
    activeTrip: Trip?,
    allTrips: List<Trip>,
    showRoadsidePassengers: Boolean = true,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val infiniteTransition = rememberInfiniteTransition(label = "gps_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.8f, 3.5f)
                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-400f, 400f),
                        y = (offset.y + pan.y).coerceIn(-400f, 400f)
                    )
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Background subtle grid
            val gridSpacing = 40f * scale
            val gridPaint = Color(0xFF1E293B).copy(alpha = 0.4f)
            var gx = (offset.x % gridSpacing)
            while (gx < width) {
                drawLine(
                    color = gridPaint,
                    start = Offset(gx, 0f),
                    end = Offset(gx, height),
                    strokeWidth = 1f
                )
                gx += gridSpacing
            }
            var gy = (offset.y % gridSpacing)
            while (gy < height) {
                drawLine(
                    color = gridPaint,
                    start = Offset(0f, gy),
                    end = Offset(width, gy),
                    strokeWidth = 1f
                )
                gy += gridSpacing
            }

            // Function to map normalized coordinates to canvas space with pan/zoom
            fun toScreenPos(normX: Float, normY: Float): Offset {
                val cx = width / 2f
                val cy = height / 2f
                val mappedX = normX * width
                val mappedY = normY * height
                val transformedX = (mappedX - cx) * scale + cx + offset.x
                val transformedY = (mappedY - cy) * scale + cy + offset.y
                return Offset(transformedX, transformedY)
            }

            // Draw Autoroute du Nord Corridor (Abidjan -> Attinguié -> Singrobo -> Toumodi -> Yamoussoukro -> Bouaké -> Korhogo)
            val autorouteStops = listOf(
                Pair(0.60f, 0.89f), // Abidjan
                Pair(0.56f, 0.81f), // Attinguié
                Pair(0.54f, 0.73f), // Singrobo
                Pair(0.52f, 0.65f), // Toumodi
                Pair(0.48f, 0.56f), // Yamoussoukro
                Pair(0.51f, 0.48f), // Tiébissou
                Pair(0.52f, 0.38f), // Bouaké
                Pair(0.50f, 0.28f), // Katiola
                Pair(0.48f, 0.12f)  // Korhogo
            )

            // Draw Highway Glow & Core Road Path
            val autoroutePath = Path()
            val firstScreenPt = toScreenPos(autorouteStops[0].first, autorouteStops[0].second)
            autoroutePath.moveTo(firstScreenPt.x, firstScreenPt.y)
            for (i in 1 until autorouteStops.size) {
                val pt = toScreenPos(autorouteStops[i].first, autorouteStops[i].second)
                autoroutePath.lineTo(pt.x, pt.y)
            }

            // Highway Glow
            drawPath(
                path = autoroutePath,
                color = Color(0xFFF59E0B).copy(alpha = 0.25f),
                style = Stroke(width = 16f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Highway Main Road
            drawPath(
                path = autoroutePath,
                color = Color(0xFF334155),
                style = Stroke(width = 8f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Road Central Dashed Line
            drawPath(
                path = autoroutePath,
                color = BrandAmber,
                style = Stroke(
                    width = 2.5f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // Draw Coastal Route (Abidjan -> Dabou -> Grand-Lahou -> San-Pédro)
            val coastalStops = listOf(
                Pair(0.60f, 0.89f), // Abidjan
                Pair(0.42f, 0.89f), // Dabou
                Pair(0.32f, 0.91f), // Grand-Lahou
                Pair(0.18f, 0.92f)  // San-Pédro
            )
            val coastalPath = Path()
            val coastFirst = toScreenPos(coastalStops[0].first, coastalStops[0].second)
            coastalPath.moveTo(coastFirst.x, coastFirst.y)
            for (i in 1 until coastalStops.size) {
                val pt = toScreenPos(coastalStops[i].first, coastalStops[i].second)
                coastalPath.lineTo(pt.x, pt.y)
            }
            drawPath(
                path = coastalPath,
                color = Color(0xFF0D9488).copy(alpha = 0.3f),
                style = Stroke(width = 12f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = coastalPath,
                color = Color(0xFF1E293B),
                style = Stroke(width = 6f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw Stations, Toll Booths and Major Cities
            val nativeCanvas = drawContext.canvas.nativeCanvas
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f * scale.coerceIn(0.8f, 1.4f)
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val subTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 22f * scale.coerceIn(0.8f, 1.4f)
                isAntiAlias = true
            }

            IVORY_COAST_MAP_STATIONS.forEach { station ->
                val pos = toScreenPos(station.normX, station.normY)

                when (station.type) {
                    StationType.MAJOR_CITY -> {
                        // Outer station ring
                        drawCircle(
                            color = BrandAmber,
                            radius = 9f * scale,
                            center = pos
                        )
                        drawCircle(
                            color = NavyDark,
                            radius = 5f * scale,
                            center = pos
                        )
                    }
                    StationType.TOLL_BOOTH -> {
                        drawRect(
                            color = Color(0xFFEF4444),
                            topLeft = Offset(pos.x - 6f * scale, pos.y - 6f * scale),
                            size = androidx.compose.ui.geometry.Size(12f * scale, 12f * scale)
                        )
                    }
                    StationType.HIGHWAY_STOP -> {
                        drawCircle(
                            color = Color(0xFF94A3B8),
                            radius = 5f * scale,
                            center = pos
                        )
                    }
                }

                // Station label
                nativeCanvas.drawText(
                    station.name,
                    pos.x + 14f * scale,
                    pos.y + 6f * scale,
                    if (station.type == StationType.MAJOR_CITY) textPaint else subTextPaint
                )
            }

            // Draw Roadside Passengers Waiting along the highway
            if (showRoadsidePassengers) {
                val roadsideSpots = listOf(
                    Pair(0.53f, 0.69f) to "2 passagers (Toumodi Sud)",
                    Pair(0.55f, 0.77f) to "1 passager (Singrobo Nord)",
                    Pair(0.58f, 0.85f) to "3 passagers (Attinguié Gare)"
                )
                roadsideSpots.forEach { (coord, label) ->
                    val rPos = toScreenPos(coord.first, coord.second)

                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = 6f * scale,
                        center = rPos
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = 0.3f),
                        radius = 14f * scale,
                        center = rPos
                    )

                    val passengerTextPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#38BDF8")
                        textSize = 20f * scale.coerceIn(0.8f, 1.2f)
                        isAntiAlias = true
                    }
                    nativeCanvas.drawText(
                        "👤 $label",
                        rPos.x + 12f * scale,
                        rPos.y - 8f * scale,
                        passengerTextPaint
                    )
                }
            }

            // Draw Active Buses with Real-Time Position and Radar Pulse
            allTrips.forEach { trip ->
                // Map trip latitude & longitude approximately to our normalized road canvas
                val isSelectedBus = activeTrip?.id == trip.id

                // Position calculation based on status/coordinates
                val (normX, normY) = when (trip.id) {
                    "trip_abj_bouake_01" -> Pair(0.52f, 0.65f) // Near Toumodi
                    "trip_abj_yakro_01" -> Pair(0.54f, 0.75f)  // Near Singrobo
                    "trip_abj_sanpedro_01" -> Pair(0.38f, 0.90f) // Dabou
                    "trip_abj_korhogo_01" -> Pair(0.59f, 0.88f) // Adjamé departure
                    else -> Pair(0.52f, 0.60f)
                }

                val busPos = toScreenPos(normX, normY)

                // Pulse Radar Animation
                drawCircle(
                    color = (if (isSelectedBus) BrandAmber else EmeraldSuccess).copy(alpha = pulseAlpha),
                    radius = pulseRadius * scale,
                    center = busPos
                )

                // Outer Bus Badge
                drawCircle(
                    color = if (isSelectedBus) BrandAmber else EmeraldSuccess,
                    radius = (if (isSelectedBus) 14f else 11f) * scale,
                    center = busPos
                )
                // Inner Bus Core
                drawCircle(
                    color = NavyDark,
                    radius = (if (isSelectedBus) 9f else 7f) * scale,
                    center = busPos
                )

                // Draw Bus Plate & Speed Tag
                val busTagPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor(if (isSelectedBus) "#F59E0B" else "#10B981")
                    textSize = 24f * scale.coerceIn(0.8f, 1.3f)
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                val speedText = if (trip.status == com.example.data.models.TripStatus.IN_TRANSIT) " • ${trip.speedKmH} km/h" else " • Arrêt"
                nativeCanvas.drawText(
                    "🚍 ${trip.companyName} (${trip.busPlateNumber}$speedText)",
                    busPos.x + 18f * scale,
                    busPos.y - 12f * scale,
                    busTagPaint
                )
            }
        }
    }
}
