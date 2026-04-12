package com.thomaskioko.root.model

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import kotlinx.serialization.Serializable

@Serializable
public data class ThemeState(
    val isFetching: Boolean = true,
    val appTheme: AppTheme = AppTheme.SYSTEM_THEME,
)
