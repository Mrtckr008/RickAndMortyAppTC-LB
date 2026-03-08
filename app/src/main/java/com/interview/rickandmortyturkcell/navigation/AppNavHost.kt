package com.interview.rickandmortyturkcell.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.interview.rickandmortyturkcell.ui.characterdetail.CharacterDetailRoute
import com.interview.rickandmortyturkcell.ui.characterlist.CharacterListRoute
import com.interview.rickandmortyturkcell.ui.photodetail.PhotoDetailRoute
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AppDestinations {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail"
    const val PHOTO_DETAIL = "photo_detail"

    const val CHARACTER_ID_ARG = "characterId"
    const val PHOTO_URL_ARG = "photoUrl"
    const val PHOTO_NAME_ARG = "photoName"

    const val CHARACTER_DETAIL_ROUTE = "$CHARACTER_DETAIL/{$CHARACTER_ID_ARG}"
    const val PHOTO_DETAIL_ROUTE =
        "$PHOTO_DETAIL?$PHOTO_URL_ARG={$PHOTO_URL_ARG}&$PHOTO_NAME_ARG={$PHOTO_NAME_ARG}"

    fun characterDetailRoute(characterId: Int): String {
        return "$CHARACTER_DETAIL/$characterId"
    }

    fun photoDetailRoute(photoUrl: String, photoName: String): String {
        return buildString {
            append(PHOTO_DETAIL)
            append("?")
            append(PHOTO_URL_ARG)
            append("=")
            append(photoUrl.urlEncode())
            append("&")
            append(PHOTO_NAME_ARG)
            append("=")
            append(photoName.urlEncode())
        }
    }

    private fun String.urlEncode(): String {
        return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = AppDestinations.CHARACTER_LIST,
            modifier = modifier
        ) {
            composable(
                route = AppDestinations.CHARACTER_LIST
            ) {
                CharacterListRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onCharacterClick = { characterId ->
                        navController.navigate(
                            AppDestinations.characterDetailRoute(characterId)
                        )
                    },
                    onGalleryItemClick = { uri, name ->
                        navController.navigate(
                            AppDestinations.photoDetailRoute(
                                photoUrl = uri.toString(), photoName = name
                            )
                        )
                    })
            }

            composable(
                route = AppDestinations.CHARACTER_DETAIL_ROUTE, arguments = listOf(
                navArgument(AppDestinations.CHARACTER_ID_ARG) {
                    type = NavType.IntType
                })) {
                CharacterDetailRoute(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onOpenPhoto = { photoUrl, photoName ->
                        navController.navigate(
                            AppDestinations.photoDetailRoute(
                                photoUrl = photoUrl, photoName = photoName
                            )
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    })
            }

            composable(
                route = AppDestinations.PHOTO_DETAIL_ROUTE,
                arguments = listOf(navArgument(AppDestinations.PHOTO_URL_ARG) {
                    type = NavType.StringType
                }, navArgument(AppDestinations.PHOTO_NAME_ARG) {
                    type = NavType.StringType
                })) {
                PhotoDetailRoute(
                    onBackClick = {
                        navController.popBackStack()
                    })
            }
        }
    }
}
