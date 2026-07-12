package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

public data class LayoutPreferences(
    val hapticFeedbackEnabled: Boolean,
    val seasonSortOrder: SeasonSortOrder,
    val blurImage: Boolean,
    val hiddenDiscoverSections: Set<DiscoverSection>,
    val fontSizePercent: Int,
)

internal fun DatastoreRepository.observeLayoutPreferences(): Flow<LayoutPreferences> =
    combine(
        observeHapticFeedbackEnabled(),
        observeSeasonSortOrder(),
        observeBlurUnwatchedEpisodeImages(),
        observeHiddenDiscoverSections(),
        observeFontSizePercent(),
    ) { hapticFeedbackEnabled, seasonSortOrder, blurImage, hiddenDiscoverSections, fontSizePercent ->
        LayoutPreferences(
            hapticFeedbackEnabled = hapticFeedbackEnabled,
            seasonSortOrder = seasonSortOrder,
            blurImage = blurImage,
            hiddenDiscoverSections = hiddenDiscoverSections,
            fontSizePercent = fontSizePercent,
        )
    }
