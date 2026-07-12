package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import kotlinx.coroutines.flow.Flow

public data class LayoutPreferences(
    val hapticFeedbackEnabled: Boolean,
    val seasonSortOrder: SeasonSortOrder,
    val blurImage: Boolean,
    val hiddenDiscoverSections: Set<DiscoverSection>,
    val fontSizePercent: Int,
    val rowPosterWidth: PosterWidth,
    val gridPosterWidth: PosterWidth,
    val posterCornerStyle: PosterCornerStyle,
)

internal fun DatastoreRepository.observeLayoutPreferences(): Flow<LayoutPreferences> =
    combine(
        observeHapticFeedbackEnabled(),
        observeSeasonSortOrder(),
        observeBlurUnwatchedEpisodeImages(),
        observeHiddenDiscoverSections(),
        observeFontSizePercent(),
        observeRowPosterWidth(),
        observeGridPosterWidth(),
        observePosterCornerStyle(),
    ) { hapticFeedbackEnabled, seasonSortOrder, blurImage, hiddenDiscoverSections, fontSizePercent, rowPosterWidth, gridPosterWidth, posterCornerStyle ->
        LayoutPreferences(
            hapticFeedbackEnabled = hapticFeedbackEnabled,
            seasonSortOrder = seasonSortOrder,
            blurImage = blurImage,
            hiddenDiscoverSections = hiddenDiscoverSections,
            fontSizePercent = fontSizePercent,
            rowPosterWidth = rowPosterWidth,
            gridPosterWidth = gridPosterWidth,
            posterCornerStyle = posterCornerStyle,
        )
    }
