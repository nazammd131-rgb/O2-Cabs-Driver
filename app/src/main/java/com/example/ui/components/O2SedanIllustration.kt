package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.O2Yellow

@Composable
fun O2SedanIllustration(
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.55f // Center of the car structure

        // 1. Draw Golden-Yellow Curve/Arc at the bottom
        // A beautiful thick crescent arc representing the golden road/horizon of O2 Cabs
        drawArc(
            color = O2Yellow,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.45f, cy - h * 0.15f),
            size = Size(w * 0.9f, h * 0.8f),
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )

        // Draw a dark accent line beneath the golden-yellow horizon arc
        drawArc(
            color = Color.Black.copy(alpha = 0.12f),
            startAngle = 15f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.45f, cy - h * 0.12f),
            size = Size(w * 0.9f, h * 0.82f),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // 2. Perspective Road Lines stretching from center downwards
        val roadCol = O2Yellow.copy(alpha = 0.6f)
        drawLine(
            color = roadCol,
            start = Offset(cx - 30f, cy + 45f),
            end = Offset(cx - w * 0.35f, h * 0.95f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = roadCol,
            start = Offset(cx + 30f, cy + 45f),
            end = Offset(cx + w * 0.35f, h * 0.95f),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        
        // Dashed center lane markings
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(cx, cy + 45f),
            end = Offset(cx, cy + 70f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(cx, cy + 85f),
            end = Offset(cx, cy + 115f),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )

        // 3. Realistic Soft Ground Shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.28f),
            topLeft = Offset(cx - 105f, cy + 28f),
            size = Size(210f, 18f)
        )

        // 4. Car Wheels (Pneumatic Tires)
        // Left wheel
        drawRoundRect(
            color = Color(0xFF1E1E1F),
            topLeft = Offset(cx - 96f, cy + 10f),
            size = Size(24f, 32f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Right wheel
        drawRoundRect(
            color = Color(0xFF1E1E1F),
            topLeft = Offset(cx + 72f, cy + 10f),
            size = Size(24f, 32f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Sleek Sport Alloys/Hubcaps
        drawCircle(
            color = Color.LightGray,
            radius = 8f,
            center = Offset(cx - 84f, cy + 26f)
        )
        drawCircle(
            color = Color.LightGray,
            radius = 8f,
            center = Offset(cx + 84f, cy + 26f)
        )

        // 5. Sleek Rearview Mirrors
        // Left mirror
        val leftMirror = Path().apply {
            moveTo(cx - 80f, cy - 25f)
            quadraticTo(cx - 105f, cy - 30f, cx - 105f, cy - 22f)
            lineTo(cx - 82f, cy - 16f)
            close()
        }
        drawPath(path = leftMirror, color = Color(0xFFE5E5EA))
        drawPath(path = leftMirror, color = Color(0xFF1F1F21), style = Stroke(width = 2f))

        // Right mirror
        val rightMirror = Path().apply {
            moveTo(cx + 80f, cy - 25f)
            quadraticTo(cx + 105f, cy - 30f, cx + 105f, cy - 22f)
            lineTo(cx + 82f, cy - 16f)
            close()
        }
        drawPath(path = rightMirror, color = Color(0xFFE5E5EA))
        drawPath(path = rightMirror, color = Color(0xFF1F1F21), style = Stroke(width = 2f))

        // 6. Main Car Lower Body Path (Front View perspective)
        val bodyPath = Path().apply {
            moveTo(cx - 82f, cy + 26f) // bottom left
            lineTo(cx - 92f, cy + 16f)
            lineTo(cx - 92f, cy - 2f)
            lineTo(cx - 84f, cy - 14f)
            lineTo(cx - 72f, cy - 18f)
            lineTo(cx - 55f, cy - 22f) // hood line start
            lineTo(cx + 55f, cy - 22f) // hood line end
            lineTo(cx + 72f, cy - 18f)
            lineTo(cx + 84f, cy - 14f)
            lineTo(cx + 92f, cy - 2f)
            lineTo(cx + 92f, cy + 16f)
            lineTo(cx + 82f, cy + 26f)
            close()
        }
        drawPath(path = bodyPath, color = Color.White)
        drawPath(path = bodyPath, color = Color(0xFFC7C7CC), style = Stroke(width = 2.5f))

        // 7. Glossy Jet-Black Tinted Windshield & Cabin
        val cabinPath = Path().apply {
            moveTo(cx - 52f, cy - 22f) // bottom left windshield
            lineTo(cx - 36f, cy - 48f) // top left
            lineTo(cx + 36f, cy - 48f) // top right
            lineTo(cx + 52f, cy - 22f) // bottom right windshield
            close()
        }
        drawPath(path = cabinPath, color = Color(0xFF1C1C1E))
        
        // Windshield light shearing flare gradient
        val sheenBrush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.40f), Color.Transparent, Color.White.copy(alpha = 0.08f)),
            start = Offset(cx - 40f, cy - 48f),
            end = Offset(cx + 20f, cy - 22f)
        )
        drawPath(path = cabinPath, brush = sheenBrush)

        // Center split panel line (interior accent)
        drawLine(
            color = Color.White.copy(alpha = 0.15f),
            start = Offset(cx, cy - 48f),
            end = Offset(cx, cy - 22f),
            strokeWidth = 2.5f
        )

        // 8. Bumper & Sports Radiator Grille
        val grillPath = Path().apply {
            moveTo(cx - 65f, cy - 2f)
            lineTo(cx + 65f, cy - 2f)
            lineTo(cx + 55f, cy + 12f)
            lineTo(cx - 55f, cy + 12f)
            close()
        }
        drawPath(path = grillPath, color = Color(0xFF101011))

        // Sporty grill mesh slots
        for (i in -4..4) {
            val offsetVal = i * 11f
            drawLine(
                color = Color.DarkGray,
                start = Offset(cx + offsetVal - 4f, cy - 2f),
                end = Offset(cx + offsetVal + 4f, cy + 12f),
                strokeWidth = 1.5f
            )
        }

        // Front bumper lower spoiler/valence lip
        val lowerLipPath = Path().apply {
            moveTo(cx - 82f, cy + 18f)
            lineTo(cx - 68f, cy + 28f)
            lineTo(cx + 68f, cy + 28f)
            lineTo(cx + 82f, cy + 18f)
            lineTo(cx + 62f, cy + 24f)
            lineTo(cx - 62f, cy + 24f)
            close()
        }
        drawPath(path = lowerLipPath, color = Color(0xFFE5E5EA))

        // 9. Sharp Xenon Headlights
        val leftHeadlight = Path().apply {
            moveTo(cx - 82f, cy - 6f)
            lineTo(cx - 66f, cy - 5f)
            lineTo(cx - 62f, cy + 3f)
            lineTo(cx - 78f, cy + 5f)
            close()
        }
        val rightHeadlight = Path().apply {
            moveTo(cx + 82f, cy - 6f)
            lineTo(cx + 66f, cy - 5f)
            lineTo(cx + 62f, cy + 3f)
            lineTo(cx + 78f, cy + 5f)
            close()
        }

        // Active glowing headlamps
        drawPath(path = leftHeadlight, color = Color.White)
        drawPath(path = leftHeadlight, color = O2Yellow, style = Stroke(width = 2f))
        drawPath(path = rightHeadlight, color = Color.White)
        drawPath(path = rightHeadlight, color = O2Yellow, style = Stroke(width = 2f))

        // Light beams shining out (Gradients)
        val beamColStart = O2Yellow.copy(alpha = 0.40f)
        val beamColEnd = Color.Transparent
        
        val leftBeamBrush = Brush.linearGradient(
            colors = listOf(beamColStart, beamColEnd),
            start = Offset(cx - 72f, cy),
            end = Offset(cx - 150f, cy + 90f)
        )
        val leftBeamPath = Path().apply {
            moveTo(cx - 78f, cy)
            lineTo(cx - 180f, cy + 70f)
            lineTo(cx - 100f, cy + 100f)
            lineTo(cx - 64f, cy + 4f)
            close()
        }
        drawPath(path = leftBeamPath, brush = leftBeamBrush)

        val rightBeamBrush = Brush.linearGradient(
            colors = listOf(beamColStart, beamColEnd),
            start = Offset(cx + 72f, cy),
            end = Offset(cx + 150f, cy + 90f)
        )
        val rightBeamPath = Path().apply {
            moveTo(cx + 78f, cy)
            lineTo(cx + 180f, cy + 70f)
            lineTo(cx + 100f, cy + 100f)
            lineTo(cx + 64f, cy + 4f)
            close()
        }
        drawPath(path = rightBeamPath, brush = rightBeamBrush)

        // 10. Yellow metallic license plate
        drawRoundRect(
            color = O2Yellow,
            topLeft = Offset(cx - 24f, cy + 14f),
            size = Size(48f, 15f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(cx - 24f, cy + 14f),
            size = Size(48f, 15f),
            cornerRadius = CornerRadius(3f, 3f),
            style = Stroke(width = 1.5f)
        )

        // Plate Text
        drawText(
            textMeasurer = textMeasurer,
            text = "O2 CABS",
            style = TextStyle(
                color = Color.Black,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            ),
            topLeft = Offset(cx - 16f, cy + 14.5f)
        )

        // Sleek Hood Contours
        drawLine(
            color = Color(0xFFFFCC00),
            start = Offset(cx - 25f, cy - 21f),
            end = Offset(cx - 30f, cy - 8f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFFFFCC00),
            start = Offset(cx + 25f, cy - 21f),
            end = Offset(cx + 30f, cy - 8f),
            strokeWidth = 2f
        )
    }
}
