package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public data class LayoutPreferences(
    val hapticFeedbackEnabled: Boolean,
)

internal fun DatastoreRepository.observeLayoutPreferences(): Flow<LayoutPreferences> =
    observeHapticFeedbackEnabled().map { hapticFeedbackEnabled ->
        LayoutPreferences(hapticFeedbackEnabled = hapticFeedbackEnabled)
    }
