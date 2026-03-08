package com.interview.rickandmortyturkcell.ui.characterlist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.South
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterGridUiModel
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterListUiState
import com.interview.rickandmortyturkcell.ui.characterlist.widgets.AppendRetryItem
import com.interview.rickandmortyturkcell.ui.characterlist.widgets.TopBarActionChip
import com.interview.rickandmortyturkcell.ui.common.LoadingItem
import com.interview.rickandmortyturkcell.ui.common.theme.RickAndMortyTurkcellTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun rememberGridColumnCount(): Int {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return when {
        screenWidthDp >= 900 -> 4
        screenWidthDp >= 600 -> 3
        else -> 2
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterListUiState,
    galleryItems: List<CharacterGridUiModel.GalleryItem>,
    characterItems: List<CharacterGridUiModel.CharacterItem>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPhotosPicked: (List<Uri>) -> Unit,
    onSortOrderChanged: (CharacterListSortOrder) -> Unit,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryAppend: () -> Unit,
    onGridItemClick: (CharacterGridUiModel) -> Unit,
    onErrorConsumed: () -> Unit,
    onAppendErrorConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = rememberGridColumnCount()
    val context = LocalContext.current

    val previousGalleryCount = remember { mutableIntStateOf(galleryItems.size) }

    val screenTitle = stringResource(R.string.character_list_title)
    val galleryHeader = stringResource(R.string.gallery_photos)
    val characterHeader = stringResource(R.string.rick_and_morty_characters)
    val pickPhotosCd = stringResource(R.string.pick_photos)
    val toggleSortCd = stringResource(R.string.toggle_sort_order)

    val sortLabel = when (uiState.characterListSortOrder) {
        CharacterListSortOrder.NEWEST_FIRST -> stringResource(R.string.sort_newest_first)
        CharacterListSortOrder.OLDEST_FIRST -> stringResource(R.string.sort_oldest_first)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val distinctUris = uris.distinct()
        if (distinctUris.isNotEmpty()) {
            distinctUris.forEach { uri ->
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            onPhotosPicked(distinctUris)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            onErrorConsumed()
        }
    }

    LaunchedEffect(uiState.appendErrorMessage) {
        uiState.appendErrorMessage?.let {
            onAppendErrorConsumed()
        }
    }

    Scaffold(
        modifier = modifier, topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = screenTitle, style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = sortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ), actions = {
                    TopBarActionChip(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = pickPhotosCd
                        )
                    }

                    TopBarActionChip(
                        onClick = {
                            val nextSort = when (uiState.characterListSortOrder) {
                                CharacterListSortOrder.NEWEST_FIRST -> CharacterListSortOrder.OLDEST_FIRST
                                CharacterListSortOrder.OLDEST_FIRST -> CharacterListSortOrder.NEWEST_FIRST
                            }
                            onSortOrderChanged(nextSort)
                        }) {
                        Icon(
                            imageVector = when (uiState.characterListSortOrder) {
                                CharacterListSortOrder.NEWEST_FIRST -> Icons.Rounded.South
                                CharacterListSortOrder.OLDEST_FIRST -> Icons.Rounded.North
                            }, contentDescription = toggleSortCd
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                })
        }) { innerPadding ->

        key(uiState.listResetKey) {
            val gridState = rememberSaveable(
                uiState.listResetKey, saver = LazyGridState.Saver
            ) {
                LazyGridState()
            }

            LaunchedEffect(
                gridState,
                characterItems,
                uiState.isAppending,
                uiState.endReached,
                uiState.isRefreshing,
                uiState.isInitialLoading,
                uiState.appendErrorMessage
            ) {
                snapshotFlow {
                    val visibleKeys =
                        gridState.layoutInfo.visibleItemsInfo.mapNotNull { it.key as? String }
                            .toSet()

                    val lastCharacterKey = characterItems.lastOrNull()?.uniqueKey
                    lastCharacterKey != null && visibleKeys.contains(lastCharacterKey)
                }.distinctUntilChanged().collect { isLastCharacterVisible ->
                    val shouldLoadMore =
                        isLastCharacterVisible && characterItems.isNotEmpty() && !uiState.isAppending && !uiState.endReached && !uiState.isRefreshing && !uiState.isInitialLoading && uiState.appendErrorMessage == null

                    if (shouldLoadMore) {
                        onLoadNextPage()
                    }
                }
            }

            LaunchedEffect(galleryItems.size) {
                val hadItemsBefore = previousGalleryCount.intValue
                val hasNewItems = galleryItems.size > hadItemsBefore

                if (hasNewItems) {
                    gridState.scrollToItem(0)
                }

                previousGalleryCount.intValue = galleryItems.size
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing && !uiState.isInitialLoading,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (galleryItems.isNotEmpty()) {
                            item(
                                span = { GridItemSpan(columns) }, key = "gallery_header"
                            ) {
                                Text(
                                    text = galleryHeader,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                            }

                            items(
                                items = galleryItems,
                                key = { it.uniqueKey },
                                contentType = { "gallery" }) { item ->
                                CharacterGridItem(
                                    item = item,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onClick = onGridItemClick
                                )
                            }

                            item(
                                span = { GridItemSpan(columns) }, key = "character_header"
                            ) {
                                Text(
                                    text = characterHeader,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }

                        items(
                            items = characterItems,
                            key = { it.uniqueKey },
                            contentType = { "character" }) { item ->
                            CharacterGridItem(
                                item = item,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onClick = onGridItemClick
                            )
                        }

                        if (uiState.isAppending) {
                            item(
                                span = { GridItemSpan(columns) }, key = "append_loading"
                            ) {
                                LoadingItem(
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            }
                        }

                        if (uiState.appendErrorMessage != null) {
                            item(
                                span = { GridItemSpan(columns) }, key = "append_retry"
                            ) {
                                AppendRetryItem(
                                    message = uiState.appendErrorMessage, onRetry = onRetryAppend
                                )
                            }
                        }
                    }

                    if (uiState.isInitialLoading) {
                        LoadingItem(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Preview(
    name = "Character List Screen", showBackground = true, showSystemUi = true
)
@Composable
private fun CharacterListScreenPreview() {
    RickAndMortyTurkcellTheme {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = "list", label = "character_list_preview"
            ) {
                CharacterListScreen(
                    uiState = CharacterListUiState(
                        characterListSortOrder = CharacterListSortOrder.NEWEST_FIRST,
                        characterItems = previewCharacterItems,
                        isInitialLoading = false,
                        isRefreshing = false,
                        isAppending = false,
                        endReached = false,
                        errorMessage = null,
                        appendErrorMessage = null,
                        listResetKey = 0
                    ),
                    galleryItems = previewGalleryItems,
                    characterItems = previewCharacterItems,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onPhotosPicked = {},
                    onSortOrderChanged = {},
                    onRefresh = {},
                    onLoadNextPage = {},
                    onRetryAppend = {},
                    onGridItemClick = {},
                    onErrorConsumed = {},
                    onAppendErrorConsumed = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private val previewCharacterItems = listOf(
    CharacterGridUiModel.CharacterItem(
        id = 1,
        name = "Rick Sanchez",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        status = CharacterStatus.ALIVE,
        species = CharacterSpecies.HUMAN,
        gender = CharacterGender.MALE
    ), CharacterGridUiModel.CharacterItem(
        id = 2,
        name = "Morty Smith",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        status = CharacterStatus.ALIVE,
        species = CharacterSpecies.HUMAN,
        gender = CharacterGender.MALE
    ), CharacterGridUiModel.CharacterItem(
        id = 248,
        name = "Adjudicator Rick",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/248.jpeg",
        status = CharacterStatus.DEAD,
        species = CharacterSpecies.HUMAN,
        gender = CharacterGender.MALE
    ), CharacterGridUiModel.CharacterItem(
        id = 14,
        name = "Alien Morty",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/14.jpeg",
        status = CharacterStatus.UNKNOWN,
        species = CharacterSpecies.ALIEN,
        gender = CharacterGender.MALE
    )
)

private val previewGalleryItems = listOf(
    CharacterGridUiModel.GalleryItem(
        uri = "content://preview/gallery/1",
        displayName = "Picked Photo 1",
        dateTakenMillis = 1710000000000
    ), CharacterGridUiModel.GalleryItem(
        uri = "content://preview/gallery/2",
        displayName = "Picked Photo 2",
        dateTakenMillis = 1710000001000
    )
)