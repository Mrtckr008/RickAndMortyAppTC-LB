package com.interview.rickandmortyturkcell.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logCharacterDetailOpened(characterId: Int, characterName: String) {
        firebaseAnalytics.logEvent(
            AnalyticsEvent.CHARACTER_DETAIL_OPENED, bundleOf(
                AnalyticsParam.CHARACTER_ID to characterId,
                AnalyticsParam.CHARACTER_NAME to characterName
            )
        )
    }

    fun logPhotoDownloaded(characterName: String) {
        firebaseAnalytics.logEvent(
            AnalyticsEvent.CHARACTER_PHOTO_DOWNLOADED, bundleOf(
                AnalyticsParam.CHARACTER_NAME to characterName
            )
        )
    }

    private fun bundleOf(vararg pairs: Pair<String, Any?>): Bundle {
        return Bundle().apply {
            pairs.forEach { (key, value) ->
                when (value) {
                    null -> Unit
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
    }
}

private object AnalyticsEvent {
    const val CHARACTER_DETAIL_OPENED = "character_detail_opened"
    const val CHARACTER_PHOTO_DOWNLOADED = "character_photo_downloaded"
}

private object AnalyticsParam {
    const val CHARACTER_ID = "character_id"
    const val CHARACTER_NAME = "character_name"
}
