package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.ForestGreenLight

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "aceptado", "pagada", "finalizado", "completada" -> {
            Color(0xFFD1FAE5) to Color(0xFF065F46)
        }
        "enviado", "en curso", "parcial" -> {
            Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        }
        "pendiente", "en preparación", "planificado" -> {
            Color(0xFFFEF3C7) to Color(0xFF92400E)
        }
        "rechazado", "cancelado", "vencida", "urgente" -> {
            Color(0xFFFEE2E2) to Color(0xFF991B1B)
        }
        "visto" -> {
            Color(0xFFEDE9FE) to Color(0xFF5B21B6)
        }
        else -> {
            Color(0xFFF1F5F9) to Color(0xFF475569)
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Text(
                text = status,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (color, bg) = when (priority.lowercase()) {
        "urgente" -> CrimsonError to Color(0xFFFEE2E2)
        "alta" -> AmberWarning to Color(0xFFFEF3C7)
        "media" -> BlueInfo to Color(0xFFDBEAFE)
        else -> ForestGreenLight to Color(0xFFD1FAE5)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = priority,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
