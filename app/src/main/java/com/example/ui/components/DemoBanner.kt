package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning

@Composable
fun DemoBanner(
    remainingSeconds: Int,
    onExitDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFEF3C7)) // Amber light
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.HourglassTop,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Modo Demo Activo • Tiempo restante: ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF92400E)
            )
            Text(
                text = formattedTime,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309)
            )
        }

        TextButton(onClick = onExitDemo) {
            Text(
                text = "Salir",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309)
            )
        }
    }
}
