package com.thomaskioko.root.model

import com.thomaskioko.tvmaniac.domain.theme.Theme
import kotlinx.serialization.Serializable

@Serializable
public data class ThemeState(
    val isFetching: Boolean = true,
    val appTheme: Theme = Theme.SYSTEM_THEME,
)
