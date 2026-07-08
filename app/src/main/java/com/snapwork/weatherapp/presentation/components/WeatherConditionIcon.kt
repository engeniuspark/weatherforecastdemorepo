package com.snapwork.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun WeatherConditionIcon(
    condition: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 100.dp
) {
    val conditionLower = condition.lowercase()
    
    // Determine the icon and custom premium gradient background based on weather type
    val (icon, tint, backgroundGradient) = when {
        conditionLower.contains("rain") -> {
            Triple(
                Icons.Default.Grain,
                Color(0xFF64B5F6),
                Brush.linearGradient(listOf(Color(0xFF1E3C72), Color(0xFF2A5298)))
            )
        }
        conditionLower.contains("cloud") -> {
            Triple(
                Icons.Default.Cloud,
                Color(0xFFECEFF1),
                Brush.linearGradient(listOf(Color(0xFFB0BEC5), Color(0xFF78909C)))
            )
        }
        conditionLower.contains("thunder") -> {
            Triple(
                Icons.Default.Thunderstorm,
                Color(0xFFFFD54F),
                Brush.linearGradient(listOf(Color(0xFF373B44), Color(0xFF4286F4)))
            )
        }
        conditionLower.contains("snow") -> {
            Triple(
                Icons.Default.AcUnit,
                Color(0xFFE0F7FA),
                Brush.linearGradient(listOf(Color(0xFF83A4D4), Color(0xFFB6FBFF)))
            )
        }
        conditionLower.contains("mist") || conditionLower.contains("fog") || conditionLower.contains("haze") -> {
            Triple(
                Icons.Default.Dehaze,
                Color(0xFFB0BEC5),
                Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5)))
            )
        }
        else -> { // Clear (Sun/Moon logic using local time)
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour in 6..17) { // Before 6:00 PM (6:00 AM to 5:59 PM)
                Triple(
                    Icons.Default.WbSunny,
                    Color(0xFFFFB300),
                    Brush.linearGradient(listOf(Color(0xFFFFE259), Color(0xFFFFA751)))
                )
            } else { // After 6:00 PM
                Triple(
                    Icons.Default.NightsStay,
                    Color(0xFFE0F7FA),
                    Brush.linearGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43)))
                )
            }
        }
    }

    Box(
        modifier = modifier
            .size(iconSize * 1.3f)
            .clip(CircleShape)
            .background(backgroundGradient)
            .padding(iconSize * 0.15f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Weather Icon for $condition",
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
