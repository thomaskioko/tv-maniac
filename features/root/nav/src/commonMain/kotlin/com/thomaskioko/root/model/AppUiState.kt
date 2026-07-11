package com.thomaskioko.root.model

import com.thomaskioko.tvmaniac.domain.theme.Theme
import kotlinx.serialization.Serializable

@Serializable
public data class AppUiState(
    val isFetching: Boolean = true,
    val appTheme: Theme = Theme.SYSTEM_THEME,
    val hapticFeedbackEnabled: Boolean = true,
    val blurImage: Boolean = false,
)
