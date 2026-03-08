package com.interview.rickandmortyturkcell.ui.characterdetail.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.interview.domain.model.CharacterStatus
import com.interview.rickandmortyturkcell.ui.characterdetail.toUiText

@Composable
fun StatusPill(
    status: CharacterStatus, modifier: Modifier = Modifier
) {
    val background = when (status) {
        CharacterStatus.ALIVE -> Color(0xFF1E6B44)
        CharacterStatus.DEAD -> Color(0xFF5A1F1F)
        CharacterStatus.UNKNOWN -> Color(0xFF4A4A4A)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = background.copy(alpha = 0.92f)
    ) {
        Text(
            text = status.toUiText(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun StatusDot(
    status: CharacterStatus
) {
    val color = when (status) {
        CharacterStatus.ALIVE -> Color(0xFF2BB673)
        CharacterStatus.DEAD -> Color(0xFFD9534F)
        CharacterStatus.UNKNOWN -> Color(0xFF8E8E93)
    }

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}
