package com.interview.rickandmortyturkcell.ui.characterlist.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.interview.domain.model.CharacterStatus
import com.interview.rickandmortyturkcell.R

@Composable
fun StatusBadge(
    status: CharacterStatus, modifier: Modifier = Modifier
) {
    val text = when (status) {
        CharacterStatus.ALIVE -> stringResource(R.string.status_badge_alive)
        CharacterStatus.DEAD -> stringResource(R.string.status_badge_dead)
        CharacterStatus.UNKNOWN -> ""
    }

    if (text.isBlank()) return

    Box(
        modifier = modifier
            .background(
                color = status.badgeBackgroundColor().copy(alpha = 0.92f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text, color = Color.White, style = MaterialTheme.typography.labelMedium
        )
    }
}

private fun CharacterStatus.badgeBackgroundColor(): Color {
    return when (this) {
        CharacterStatus.ALIVE -> Color(0xFF1E6B44)
        CharacterStatus.DEAD -> Color(0xFF5A1F1F)
        CharacterStatus.UNKNOWN -> Color(0xFF4A4A4A)
    }
}