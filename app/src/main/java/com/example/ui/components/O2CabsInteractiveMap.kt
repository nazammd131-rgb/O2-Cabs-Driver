package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ride
import com.example.ui.theme.O2Yellow
import com.example.ui.theme.StatusActiveRide
import com.example.ui.theme.StatusOnline
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun O2CabsInteractiveMap(
    activeRide: Ride?,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    // Current animated path completion (from 0.0f to 1.0f)
    var animationProgress by remember { mutableStateOf(0.0f) }
    
    // Manage local simulation relative to the live Ride state
    val rideId = activeRide?.id ?: ""
    val rideStatus = activeRide?.status ?: "NONE"

    // Listen to changes in status or active ride ID, and trigger/reset smooth navigation simulations
    LaunchedEffect(key1 = rideId, key2 = rideStatus) {
        if (activeRide != null && (rideStatus == "ACCEPTED" || rideStatus == "STARTED")) {
            animationProgress = 0.0f
            // Smoothly increment navigation simulation progress
            while (animationProgress < 1.0f) {
                delay(120) // Speed steps
                animationProgress += 0.015f
                if (animationProgress > 1.0f) animationProgress = 1.0f
            }
        } else {
            animationProgress = 0.0f
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141416))
            .testTag("o2_cabs_interactive_map")
    ) {
        // Render stylized Vector Map Canvas simulating active GPS lines, parks, roads
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Draw subtle grid gridlines representing city blocks and maps structure
            val gridSpacing = 60.dp.toPx()
            val gridStroke = Stroke(width = 1.dp.toPx())
            
            for (x in 0..canvasWidth.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF222225),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), canvasHeight),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (y in 0..canvasHeight.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF222225),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(canvasWidth, y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 2. Draw Simulated Main Streets
            val streetEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f)
            val roadColor = Color(0xFF2E2E33)
            
            // Major Horizontal Road
            drawLine(
                color = roadColor,
                start = Offset(0f, canvasHeight * 0.3f),
                end = Offset(canvasWidth, canvasHeight * 0.3f),
                strokeWidth = 35.dp.toPx()
            )
            // Major Diagonal Road
            drawLine(
                color = roadColor,
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 24.dp.toPx()
            )
            // Another Loop Road
            drawLine(
                color = roadColor,
                start = Offset(canvasWidth * 0.1f, canvasHeight),
                end = Offset(canvasWidth * 0.9f, 0f),
                strokeWidth = 28.dp.toPx()
            )

            // 3. Draw active routes if a ride is in progress
            if (activeRide != null) {
                // Map logical locations to canvas positions
                // Base anchors
                val pickupX = canvasWidth * 0.25f
                val pickupY = canvasHeight * 0.70f
                val dropX = canvasWidth * 0.75f
                val dropY = canvasHeight * 0.25f
                
                // Driver base starting position
                val driverStartX = canvasWidth * 0.5f
                val driverStartY = canvasHeight * 0.85f

                when (rideStatus) {
                    "ACCEPTED" -> {
                        // Drawing: Heading to pickup
                        // Route Line: From Driver position to Pickup Point
                        drawLine(
                            color = Color(0xFF42424F),
                            start = Offset(driverStartX, driverStartY),
                            end = Offset(pickupX, pickupY),
                            strokeWidth = 5.dp.toPx(),
                            pathEffect = streetEffect
                        )
                        drawLine(
                            color = O2Yellow,
                            start = Offset(driverStartX, driverStartY),
                            end = Offset(
                                lerp(driverStartX, pickupX, animationProgress),
                                lerp(driverStartY, pickupY, animationProgress)
                            ),
                            strokeWidth = 6.dp.toPx()
                        )

                        // Draw Target Pickup Pin
                        drawCircle(
                            color = StatusOnline, // Green Pin for Pickup
                            radius = 11.dp.toPx(),
                            center = Offset(pickupX, pickupY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = Offset(pickupX, pickupY)
                        )

                        // Draw moving Taxi Car Icon
                        val currentCarX = lerp(driverStartX, pickupX, animationProgress)
                        val currentCarY = lerp(driverStartY, pickupY, animationProgress)
                        
                        drawCircle(
                            color = O2Yellow,
                            radius = 16.dp.toPx(),
                            center = Offset(currentCarX, currentCarY)
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = 5.dp.toPx(),
                            center = Offset(currentCarX, currentCarY)
                        )
                    }
                    "STARTED" -> {
                        // Trip Ongoing: Pickup point to Destination Drop-off
                        drawLine(
                            color = Color(0xFF42424F),
                            start = Offset(pickupX, pickupY),
                            end = Offset(dropX, dropY),
                            strokeWidth = 5.dp.toPx(),
                            pathEffect = streetEffect
                        )
                        drawLine(
                            color = StatusActiveRide, // Blue active navigation trail
                            start = Offset(pickupX, pickupY),
                            end = Offset(
                                lerp(pickupX, dropX, animationProgress),
                                lerp(pickupY, dropY, animationProgress)
                            ),
                            strokeWidth = 6.dp.toPx()
                        )

                        // Draw Pickup circle
                        drawCircle(
                            color = Color(0x6634C759),
                            radius = 8.dp.toPx(),
                            center = Offset(pickupX, pickupY)
                        )

                        // Draw drop circle
                        drawCircle(
                            color = Color(0xFFFF3B30), // Red Pin for destination drop-off
                            radius = 11.dp.toPx(),
                            center = Offset(dropX, dropY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = Offset(dropX, dropY)
                        )

                        // Draw moving Taxi Car Icon
                        val currentCarX = lerp(pickupX, dropX, animationProgress)
                        val currentCarY = lerp(pickupY, dropY, animationProgress)
                        
                        drawCircle(
                            color = O2Yellow,
                            radius = 16.dp.toPx(),
                            center = Offset(currentCarX, currentCarY)
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = 5.dp.toPx(),
                            center = Offset(currentCarX, currentCarY)
                        )
                    }
                    else -> {
                        // Standard state pins
                        drawCircle(color = StatusOnline, radius = 9.dp.toPx(), center = Offset(pickupX, pickupY))
                        drawCircle(color = Color(0xFFFF3B30), radius = 9.dp.toPx(), center = Offset(dropX, dropY))
                    }
                }
            } else {
                // Free roaming / Idle Map Mode: Renders simple glowing dot indicating scanning for ride requests
                if (isOnline) {
                    val floatCenter = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f)
                    drawCircle(
                        color = Color(0x33FFD100),
                        radius = 40.dp.toPx(),
                        center = floatCenter
                    )
                    drawCircle(
                        color = O2Yellow,
                        radius = 8.dp.toPx(),
                        center = floatCenter
                    )
                }
            }
        }

        // Overlay Navigation Details Card
        if (activeRide != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xE6141416)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(O2Yellow, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (rideStatus == "ACCEPTED") Icons.Filled.Navigation else Icons.Filled.LocalTaxi,
                                contentDescription = "Navigation icon",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            val routeTitle = if (rideStatus == "ACCEPTED") "En Route to Pickup" else "Driving to Destination"
                            val routePlace = if (rideStatus == "ACCEPTED") activeRide.pickupLocationName else activeRide.dropLocationName
                            
                            Text(
                                text = routeTitle,
                                color = O2Yellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = routePlace,
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                    
                    // Live distance simulated counts
                    val remainingDist = remember(animationProgress, activeRide.distanceKm, rideStatus) {
                        val originalDist = if (rideStatus == "ACCEPTED") 1.8 else activeRide.distanceKm
                        val progress = if (animationProgress >= 1f) 1f else animationProgress
                        val rem = originalDist * (1.0f - progress)
                        String.format("%.1f", rem)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$remainingDist KM",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Estimated Remaining",
                            color = Color.Gray,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        } else {
            // Free driving dashboard widget
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC000000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (isOnline) StatusOnline else Color.Gray, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline) "Searching for Passenger..." else "Driver Offline",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Linear interpolation utility for coordinates
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}
