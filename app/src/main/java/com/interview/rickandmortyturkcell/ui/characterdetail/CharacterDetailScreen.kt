package com.interview.rickandmortyturkcell.ui.characterdetail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FmdGood
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Transgender
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.ui.characterdetail.model.CharacterDetailModel
import com.interview.rickandmortyturkcell.ui.characterdetail.model.CharacterDetailUiState
import com.interview.rickandmortyturkcell.ui.characterdetail.widgets.InfoCard
import com.interview.rickandmortyturkcell.ui.characterdetail.widgets.StatusDot
import com.interview.rickandmortyturkcell.ui.characterdetail.widgets.StatusPill
import com.interview.rickandmortyturkcell.ui.common.theme.RickAndMortyTurkcellTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onPhotoClick: (photoUrl: String, photoName: String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val character = uiState.character
    val scrollState = rememberScrollState()

    if (uiState.isLoading && character == null) {
        Box(
            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (character == null) {
        Box(
            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.character_detail_not_found))
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = character.imageUrl, contentDescription = stringResource(
                    R.string.character_detail_image_cd, character.name
                ), modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "character-image-${character.id}"
                            ), animatedVisibilityScope = animatedVisibilityScope
                        )
                        .clickable {
                            onPhotoClick(character.imageUrl, character.name)
                        }
                        .fillMaxSize(), contentScale = ContentScale.Crop)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.18f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = onBackClick, colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.35f), contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }

                StatusPill(status = character.status)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = character.name, style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ), color = Color.White
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                InfoCard(
                    title = stringResource(R.string.character_detail_status),
                    value = character.status.toUiText(),
                    icon = {
                        StatusDot(status = character.status)
                    })

                InfoCard(
                    title = stringResource(R.string.character_detail_species),
                    value = character.species.toUiText(),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Public, contentDescription = null
                        )
                    })

                InfoCard(
                    title = stringResource(R.string.character_detail_gender),
                    value = character.gender.toUiText(),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Transgender, contentDescription = null
                        )
                    })

                InfoCard(
                    title = stringResource(R.string.character_detail_origin),
                    value = character.originName,
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Person, contentDescription = null
                        )
                    })

                InfoCard(
                    title = stringResource(R.string.character_detail_location),
                    value = character.locationName,
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.FmdGood, contentDescription = null
                        )
                    })

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CharacterStatus.toUiText(): String {
    return when (this) {
        CharacterStatus.ALIVE -> stringResource(R.string.character_status_alive)
        CharacterStatus.DEAD -> stringResource(R.string.character_status_dead)
        CharacterStatus.UNKNOWN -> stringResource(R.string.character_status_unknown)
    }
}

@Composable
fun CharacterSpecies.toUiText(): String {
    return when (this) {
        CharacterSpecies.HUMAN -> stringResource(R.string.character_species_human)
        CharacterSpecies.ALIEN -> stringResource(R.string.character_species_alien)
        CharacterSpecies.UNKNOWN -> stringResource(R.string.character_species_unknown)
    }
}

@Composable
fun CharacterGender.toUiText(): String {
    return when (this) {
        CharacterGender.MALE -> stringResource(R.string.character_gender_male)
        CharacterGender.FEMALE -> stringResource(R.string.character_gender_female)
        CharacterGender.UNKNOWN, CharacterGender.GENDERLESS -> stringResource(R.string.character_gender_unknown)
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character Detail Screen", showBackground = true, showSystemUi = true
)
@Composable
private fun CharacterDetailScreenPreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "detail", label = "character_detail_preview"
            ) {
                CharacterDetailScreen(
                    uiState = CharacterDetailUiState(
                        isLoading = false, character = CharacterDetailModel(
                            id = 1,
                            name = "Rick Sanchez",
                            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                            status = CharacterStatus.ALIVE,
                            species = CharacterSpecies.HUMAN,
                            gender = CharacterGender.MALE,
                            originName = "Earth (C-137)",
                            locationName = "Citadel of Ricks"
                        )
                    ),
                    onPhotoClick = { _, _ -> },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onBackClick = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
