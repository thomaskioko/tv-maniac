package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.AppTheme

data class ThemeState(
  val isFetching: Boolean = true,
  val appTheme: AppTheme = AppTheme.SYSTEM_THEME,
)
