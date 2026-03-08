package com.interview.rickandmortyturkcell.ui.common

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage

@Composable
fun ZoomablePhoto(
    imageUrl: String, contentDescription: String, modifier: Modifier = Modifier
) {
    var scale by remember(imageUrl) { mutableFloatStateOf(1f) }
    var offset by remember(imageUrl) { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val minScale = 1f
    val maxScale = 4f
    val doubleTapScale = 2.5f

    fun calculateMaxOffset(currentScale: Float): Offset {
        val extraWidth = containerSize.width * (currentScale - 1f)
        val extraHeight = containerSize.height * (currentScale - 1f)
        return Offset(
            x = extraWidth / 2f, y = extraHeight / 2f
        )
    }

    fun Offset.clamp(maxOffset: Offset): Offset {
        return Offset(
            x = x.coerceIn(-maxOffset.x, maxOffset.x), y = y.coerceIn(-maxOffset.y, maxOffset.y)
        )
    }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)

        if (newScale <= minScale) {
            scale = minScale
            offset = Offset.Zero
        } else {
            val maxOffset = calculateMaxOffset(newScale)
            scale = newScale
            offset = (offset + offsetChange).clamp(maxOffset)
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .onSizeChanged { containerSize = it }
        .pointerInput(imageUrl, scale) {
            detectTapGestures(
                onDoubleTap = {
                    if (scale > minScale) {
                        scale = minScale
                        offset = Offset.Zero
                    } else {
                        scale = doubleTapScale
                        offset = Offset.Zero
                    }
                })
        }
        .transformable(state = transformableState), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
            contentScale = ContentScale.Fit
        )
    }
}
