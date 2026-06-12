package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.O2Yellow

@Composable
fun O2WelcomeBadges(
    modifier: Modifier = Modifier,
    textColor: Color = Color(0xFF151515),
    iconBgColor: Color = Color(0xFFF2F2F7),
    iconBorderColor: Color = Color(0xFFFFD100)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgeColumn(
            icon = Icons.Filled.Security,
            title = "Safe &",
            subtitle = "Secure",
            textColor = textColor,
            iconBgColor = iconBgColor,
            iconBorderColor = iconBorderColor
        )
        BadgeColumn(
            icon = Icons.Filled.AvTimer,
            title = "On Time",
            subtitle = "Every Time",
            textColor = textColor,
            iconBgColor = iconBgColor,
            iconBorderColor = iconBorderColor
        )
        BadgeColumn(
            icon = Icons.Filled.CurrencyRupee,
            title = "Best Prices",
            subtitle = "in Town",
            textColor = textColor,
            iconBgColor = iconBgColor,
            iconBorderColor = iconBorderColor
        )
    }
}

@Composable
private fun BadgeColumn(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color,
    iconBgColor: Color,
    iconBorderColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(100.dp)
    ) {
        // Rounded border badge icon wrapper
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(iconBgColor)
                .border(2.dp, iconBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
