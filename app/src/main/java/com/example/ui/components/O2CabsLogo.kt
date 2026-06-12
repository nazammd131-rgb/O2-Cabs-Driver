package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.O2Yellow
import com.example.ui.theme.O2YellowDark

@Composable
fun O2CabsLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    textColor: Color = Color.White,
    showSlogan: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Graphic Element corresponding to the signature "O2"
        Box(
            modifier = Modifier
                .size(size)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Speed Arc background + "O2" symbols
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.toPx()
                val centerOffset = Offset(w / 2, w / 2)
                
                // Draw speedometer curve decoration on top right
                drawArc(
                    color = O2Yellow,
                    startAngle = -85f,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = (w * 0.05f).coerceAtLeast(3f), pathEffect = null),
                    topLeft = Offset(w * 0.25f, w * 0.12f),
                    size = this.size * 0.65f
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // "O" - Bold Yellow Circle Ring Custom Drawing
                Box(
                    modifier = Modifier
                        .size(size * 0.45f)
                        .clip(CircleShape)
                        .background(O2Yellow)
                        .border((size * 0.08f).value.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(size * 0.22f)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.width((size * 0.04f).value.dp))

                // "2" - Stylized speed indicator digit 2
                Text(
                    text = "2",
                    fontSize = (size.value * 0.45f).sp,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // "CABS"
        Text(
            text = "CABS",
            fontSize = (size.value * 0.24f).sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Subtitle slogan "Your Daily Ride Partner"
        if (showSlogan) {
            Spacer(modifier = Modifier.height(4.dp))
            val annotatedTagline = buildAnnotatedString {
                append("Your Daily ")
                withStyle(style = SpanStyle(color = O2Yellow, fontWeight = FontWeight.Bold)) {
                    append("Ride")
                }
                append(" Partner")
            }
            Text(
                text = annotatedTagline,
                fontSize = (size.value * 0.09f).coerceAtLeast(10f).sp,
                fontWeight = FontWeight.SemiBold,
                color = if (textColor == Color.Black) Color.DarkGray else Color.LightGray,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun O2CabsBadge(
    modifier: Modifier = Modifier,
    text: String = "JAYNAGAR • MADHUBANI • BIHAR",
    textColor: Color = Color(0xFF151515),
    backgroundColor: Color = Color(0xFFFFF7C2),
    borderColor: Color = Color(0xFFFFD100)
) {
    Box(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PinDrop,
                contentDescription = "Badge pin",
                tint = Color.Black,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun O2CabsCircularEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 270.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, Color.Black, CircleShape)
            .border(6.dp, O2Yellow, CircleShape)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            // 1. "O2" Top Logo Graphic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(size * 0.22f)
            ) {
                // "O"
                Box(
                    modifier = Modifier
                        .size(size * 0.20f)
                        .clip(CircleShape)
                        .background(O2Yellow)
                        .border((size * 0.03f).value.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(size * 0.10f)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
                
                Spacer(modifier = Modifier.width(6.dp))
                
                // "2"
                Text(
                    text = "2",
                    fontSize = (size.value * 0.22f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.offset(y = (-4).dp)
                )
            }
            
            // 2. "≡ CABS ≡" with yellow wings on left and right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                // Left wings
                Column(horizontalAlignment = Alignment.End) {
                    Box(modifier = Modifier.width(18.dp).height(2.dp).background(O2Yellow))
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(12.dp).height(2.dp).background(O2Yellow))
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(6.dp).height(2.dp).background(O2Yellow))
                }
                
                Text(
                    text = " CABS ",
                    fontSize = (size.value * 0.12f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 2.sp
                )
                
                // Right wings
                Column(horizontalAlignment = Alignment.Start) {
                    Box(modifier = Modifier.width(18.dp).height(2.dp).background(O2Yellow))
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(12.dp).height(2.dp).background(O2Yellow))
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(modifier = Modifier.width(6.dp).height(2.dp).background(O2Yellow))
                }
            }
            
            // 3. YOUR DAILY RIDE PARTNER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.Black))
                Text(
                    text = " YOUR DAILY RIDE PARTNER ",
                    fontSize = (size.value * 0.038f).coerceAtLeast(8f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 0.5.sp
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.Black))
            }
            
            // 4. Detailed White Sedan Car sitting above yellow perspective roads
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                O2SedanMiniature(modifier = Modifier.fillMaxSize(), sizeMultiplier = size.value / 280f)
            }
            
            // 5. Dark road arc with "JAYNAGAR • MADHUBANI • BIHAR"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size * 0.20f)
                    .clip(RoundedCornerShape(bottomStart = 120.dp, bottomEnd = 120.dp))
                    .background(Color(0xFF151515))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Yellow perspective lines
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(14.dp).height(3.dp).background(O2Yellow))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.width(20.dp).height(3.dp).background(O2Yellow))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.width(14.dp).height(3.dp).background(O2Yellow))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "JAYNAGAR • MADHUBANI • BIHAR",
                        color = Color.White,
                        fontSize = (size.value * 0.045f).coerceAtLeast(9f).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun O2SedanMiniature(modifier: Modifier = Modifier, sizeMultiplier: Float = 1.0f) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val cx = w / 2f
        val cy = h * 0.7f
        
        // Shadow
        drawOval(
            color = Color(0x44000000),
            topLeft = Offset(cx - (120f * sizeMultiplier), cy - 6f),
            size = Size(240f * sizeMultiplier, 12f * sizeMultiplier)
        )
        
        // Road Line markings on the canvas
        drawLine(
            color = O2Yellow,
            start = Offset(cx - (40f * sizeMultiplier), cy + (15f * sizeMultiplier)),
            end = Offset(cx - (150f * sizeMultiplier), h),
            strokeWidth = 3f
        )
        drawLine(
            color = O2Yellow,
            start = Offset(cx + (40f * sizeMultiplier), cy + (15f * sizeMultiplier)),
            end = Offset(cx + (150f * sizeMultiplier), h),
            strokeWidth = 3f
        )
        
        // Car Main Lower Body
        val bodyWidth = 140f * sizeMultiplier
        val bodyHeight = 28f * sizeMultiplier
        val roofWidth = 90f * sizeMultiplier
        val roofHeight = 20f * sizeMultiplier
        
        val bodyPath = Path().apply {
            moveTo(cx - bodyWidth / 2f, cy)
            lineTo(cx - bodyWidth / 2f * 0.95f, cy - bodyHeight * 0.8f)
            lineTo(cx - bodyWidth / 2f * 0.75f, cy - bodyHeight)
            lineTo(cx + bodyWidth / 2f * 0.75f, cy - bodyHeight)
            lineTo(cx + bodyWidth / 2f * 0.95f, cy - bodyHeight * 0.8f)
            lineTo(cx + bodyWidth / 2f, cy)
            close()
        }
        drawPath(path = bodyPath, color = Color(0xFFEEEEEE))
        
        // Roof Tinted Glass
        val roofPath = Path().apply {
            moveTo(cx - bodyWidth / 2f * 0.50f, cy - bodyHeight)
            lineTo(cx - bodyWidth / 2f * 0.38f, cy - bodyHeight - roofHeight)
            lineTo(cx + bodyWidth / 2f * 0.38f, cy - bodyHeight - roofHeight)
            lineTo(cx + bodyWidth / 2f * 0.50f, cy - bodyHeight)
            close()
        }
        drawPath(path = roofPath, color = Color(0xFF202022))
        
        // Center Pillar
        drawLine(
            color = Color.White,
            start = Offset(cx, cy - bodyHeight),
            end = Offset(cx, cy - bodyHeight - roofHeight),
            strokeWidth = 4f
        )
        
        // Glow headlights
        drawCircle(color = O2Yellow, radius = 6f * sizeMultiplier, center = Offset(cx - bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f))
        drawCircle(color = O2Yellow, radius = 6f * sizeMultiplier, center = Offset(cx + bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f))
        
        // Active beam gradients!
        val leftBeam = Brush.linearGradient(
            colors = listOf(O2Yellow.copy(alpha = 0.4f), Color.Transparent),
            start = Offset(cx - bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f),
            end = Offset(cx - bodyWidth / 2f - (25f * sizeMultiplier), cy + (15f * sizeMultiplier))
        )
        val leftBeamPath = Path().apply {
            moveTo(cx - bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f)
            lineTo(cx - bodyWidth / 2f - (40f * sizeMultiplier), cy + (5f * sizeMultiplier))
            lineTo(cx - bodyWidth / 2f - (15f * sizeMultiplier), cy + (20f * sizeMultiplier))
            close()
        }
        drawPath(path = leftBeamPath, brush = leftBeam)
        
        val rightBeam = Brush.linearGradient(
            colors = listOf(O2Yellow.copy(alpha = 0.4f), Color.Transparent),
            start = Offset(cx + bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f),
            end = Offset(cx + bodyWidth / 2f + (25f * sizeMultiplier), cy + (15f * sizeMultiplier))
        )
        val rightBeamPath = Path().apply {
            moveTo(cx + bodyWidth / 2f * 0.85f, cy - bodyHeight * 0.5f)
            lineTo(cx + bodyWidth / 2f + (40f * sizeMultiplier), cy + (5f * sizeMultiplier))
            lineTo(cx + bodyWidth / 2f + (15f * sizeMultiplier), cy + (20f * sizeMultiplier))
            close()
        }
        drawPath(path = rightBeamPath, brush = rightBeam)
        
        // Wheels
        drawCircle(color = Color(0xFF222222), radius = 8f * sizeMultiplier, center = Offset(cx - bodyWidth * 0.32f, cy))
        drawCircle(color = Color(0xFF222222), radius = 8f * sizeMultiplier, center = Offset(cx + bodyWidth * 0.32f, cy))
        
        // Grill + Badge O2
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(cx - bodyWidth * 0.18f, cy - bodyHeight * 0.55f),
            size = Size(bodyWidth * 0.36f, bodyHeight * 0.35f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        
        // Tiny yellow plate
        drawRoundRect(
            color = O2Yellow,
            topLeft = Offset(cx - bodyWidth * 0.08f, cy - bodyHeight * 0.45f),
            size = Size(bodyWidth * 0.16f, bodyHeight * 0.15f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}
