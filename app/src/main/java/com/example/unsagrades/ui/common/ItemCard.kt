package com.example.unsagrades.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.unsagrades.ui.feature.dashboard.StatusIcon
import kotlin.math.roundToInt


@Composable
fun ItemCard(
    onClick: () -> Unit,
    heigh: Dp = 80.dp,
    padding: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    Box(contentAlignment = Alignment.CenterStart) {
        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 40.dp,
                bottomStart = 8.dp,
                bottomEnd = 24.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(padding)
            ) {
                content()
            }
        }
        Box(
            modifier = Modifier
                .offset(x = (-4).dp)
                .width(8.dp)
                .height(heigh)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.secondary)
        )
    }
}