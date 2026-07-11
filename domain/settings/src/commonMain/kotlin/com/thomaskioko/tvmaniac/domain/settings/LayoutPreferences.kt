package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

public data class LayoutPreferences(
    val hapticFeedbackEnabled: Boolean,
    val seasonSortOrder: SeasonSortOrder,
)

internal fun DatastoreRepository.observeLayoutPreferences(): Flow<LayoutPreferences> =
    combine(
        observeHapticFeedbackEnabled(),
        observeSeasonSortOrder(),
    ) { hapticFeedbackEnabled, seasonSortOrder ->
        LayoutPreferences(
            hapticFeedbackEnabled = hapticFeedbackEnabled,
            seasonSortOrder = seasonSortOrder,
        )
    }
