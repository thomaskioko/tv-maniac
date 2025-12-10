package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme

enum class ThemeModel(val appTheme: AppTheme) {
    SYSTEM(AppTheme.SYSTEM_THEME),
    LIGHT(AppTheme.LIGHT_THEME),
    DARK(AppTheme.DARK_THEME),
    TERMINAL(AppTheme.TERMINAL_THEME),
    AUTUMN(AppTheme.AUTUMN_THEME),
    AQUA(AppTheme.AQUA_THEME),
    AMBER(AppTheme.AMBER_THEME),
    SNOW(AppTheme.SNOW_THEME),
    CRIMSON(AppTheme.CRIMSON_THEME),
    ;

    val displayNameKey get() = appTheme.displayNameKey
    val displayOrder get() = appTheme.displayOrder

    companion object {
        val sortedByDisplayOrder: List<ThemeModel> = entries.sortedBy { it.displayOrder }
    }
}

fun AppTheme.toThemeModel(): ThemeModel =
    ThemeModel.entries.first { it.appTheme == this }

fun ThemeModel.toAppTheme(): AppTheme = appTheme
