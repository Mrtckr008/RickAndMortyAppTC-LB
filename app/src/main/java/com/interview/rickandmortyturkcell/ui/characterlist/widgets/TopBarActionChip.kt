package com.interview.rickandmortyturkcell.ui.characterlist.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBarActionChip(
    onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center, content = content
        )
    }
}
