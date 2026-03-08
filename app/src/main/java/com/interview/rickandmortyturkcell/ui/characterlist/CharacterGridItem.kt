package com.interview.rickandmortyturkcell.ui.characterlist

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.ui.characterdetail.toUiText
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterGridUiModel
import com.interview.rickandmortyturkcell.ui.characterlist.widgets.ErrorRetryContent
import com.interview.rickandmortyturkcell.ui.characterlist.widgets.StatusBadge
import com.interview.rickandmortyturkcell.ui.common.theme.RickAndMortyTurkcellTheme
import kotlinx.coroutines.delay

private const val MAX_AUTO_RETRIES = 4

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterGridItem(
    item: CharacterGridUiModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (CharacterGridUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val data = when (item) {
        is CharacterGridUiModel.CharacterItem -> item.imageUrl
        is CharacterGridUiModel.GalleryItem -> item.uri
    }

    val title = when (item) {
        is CharacterGridUiModel.CharacterItem -> item.name
        is CharacterGridUiModel.GalleryItem -> item.displayName
            ?: stringResource(R.string.gallery_photo)
    }

    val cardAlpha = when (item) {
        is CharacterGridUiModel.CharacterItem -> {
            when (item.status) {
                CharacterStatus.DEAD -> 0.72f
                else -> 1f
            }
        }

        is CharacterGridUiModel.GalleryItem -> 1f
    }

    val isRemoteImage = item is CharacterGridUiModel.CharacterItem

    var reloadVersion by remember(data) { mutableIntStateOf(0) }
    var autoRetryCount by remember(data) { mutableIntStateOf(0) }
    var pendingRetryToken by remember(data) { mutableIntStateOf(0) }
    var showErrorOverlay by remember(data) { mutableStateOf(false) }

    val request = remember(data, reloadVersion) {
        ImageRequest.Builder(context).data(data).crossfade(true).setParameter(
            key = "reload_version", value = reloadVersion, memoryCacheKey = null
        ).build()
    }

    LaunchedEffect(pendingRetryToken) {
        if (pendingRetryToken == 0) return@LaunchedEffect

        val delayMillis = autoRetryCount.retryDelayMillis()

        if (delayMillis > 0) {
            delay(delayMillis)
        }

        reloadVersion++
        pendingRetryToken = 0
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        onClick = { onClick(item) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f)
        ) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = request,
                    contentDescription = title,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = item.sharedImageKey()
                            ), animatedVisibilityScope = animatedVisibilityScope
                        )
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        autoRetryCount = 0
                        showErrorOverlay = false
                    },
                    onError = {
                        if (isRemoteImage && autoRetryCount < MAX_AUTO_RETRIES) {
                            autoRetryCount++
                            showErrorOverlay = false
                            pendingRetryToken++
                        } else {
                            showErrorOverlay = true
                        }
                    })
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(82.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            if (item is CharacterGridUiModel.CharacterItem) {
                StatusBadge(
                    status = item.status,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                )
            }

            BottomTitleRow(
                item = item,
                title = title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )

            if (showErrorOverlay) {
                ErrorRetryContent(
                    onRetry = {
                        autoRetryCount = 0
                        showErrorOverlay = false
                        reloadVersion++
                    }, modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun BottomTitleRow(
    item: CharacterGridUiModel, title: String, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title, color = Color.White, style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
        )

        when (item) {
            is CharacterGridUiModel.CharacterItem -> {
                Surface(
                    shape = CircleShape, color = Color.White.copy(alpha = 0.35f)
                ) {
                    Image(
                        painter = painterResource(id = item.species.toSpeciesIconRes()),
                        contentDescription = item.species.toUiText(),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(5.dp)
                    )
                }
            }

            is CharacterGridUiModel.GalleryItem -> {
                Surface(
                    shape = CircleShape, color = Color.Black.copy(alpha = 0.35f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Photo,
                        contentDescription = stringResource(R.string.gallery_photo),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(5.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun Int.retryDelayMillis(): Long {
    return when (this) {
        1 -> 400L
        2 -> 900L
        3 -> 1600L
        else -> 0L
    }
}

private fun CharacterGridUiModel.sharedImageKey(): String {
    return when (this) {
        is CharacterGridUiModel.CharacterItem -> "character-image-$id"
        is CharacterGridUiModel.GalleryItem -> "gallery-image-$uri"
    }
}

@DrawableRes
private fun CharacterSpecies.toSpeciesIconRes(): Int {
    return when (this) {
        CharacterSpecies.HUMAN -> R.drawable.human_icon
        CharacterSpecies.ALIEN -> R.drawable.alien_icon
        CharacterSpecies.UNKNOWN -> R.drawable.unknown_icon
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character Grid Item - Alive", showBackground = true, backgroundColor = 0xFFF5F5F5
)
@Composable
private fun CharacterGridItemAlivePreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "preview_alive", label = "character_grid_alive_preview"
            ) {
                CharacterGridItem(
                    item = CharacterGridUiModel.CharacterItem(
                        id = 1,
                        name = "Rick Sanchez",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                        status = CharacterStatus.ALIVE,
                        species = CharacterSpecies.HUMAN,
                        gender = CharacterGender.MALE
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character Grid Item - Dead", showBackground = true, backgroundColor = 0xFFF5F5F5
)
@Composable
private fun CharacterGridItemDeadPreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "preview_dead", label = "character_grid_dead_preview"
            ) {
                CharacterGridItem(
                    item = CharacterGridUiModel.CharacterItem(
                        id = 248,
                        name = "Adjudicator Rick",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/248.jpeg",
                        status = CharacterStatus.DEAD,
                        species = CharacterSpecies.HUMAN,
                        gender = CharacterGender.MALE
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character Grid Item - Alien", showBackground = true, backgroundColor = 0xFFF5F5F5
)
@Composable
private fun CharacterGridItemAlienPreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "preview_alien", label = "character_grid_alien_preview"
            ) {
                CharacterGridItem(
                    item = CharacterGridUiModel.CharacterItem(
                        id = 14,
                        name = "Alien Morty",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/14.jpeg",
                        status = CharacterStatus.UNKNOWN,
                        species = CharacterSpecies.ALIEN,
                        gender = CharacterGender.MALE
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character Grid Item - Gallery", showBackground = true, backgroundColor = 0xFFF5F5F5
)

@Composable
private fun CharacterGridItemGalleryPreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "preview_gallery", label = "character_grid_gallery_preview"
            ) {
                CharacterGridItem(
                    item = CharacterGridUiModel.GalleryItem(
                        uri = "content://preview/gallery/1",
                        displayName = "My Picked Photo",
                        dateTakenMillis = 1710000000000
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}