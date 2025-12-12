package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.i18n.StringResourceKey

public enum class ThemeModel(public val appTheme: AppTheme) {
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

    public val displayNameKey: StringResourceKey
        get() = appTheme.displayNameKey
    public val displayOrder: Int
        get() = appTheme.displayOrder

    public companion object {
        public val sortedByDisplayOrder: List<ThemeModel> = entries.sortedBy { it.displayOrder }
    }
}

public fun AppTheme.toThemeModel(): ThemeModel =
    ThemeModel.entries.first { it.appTheme == this }

public fun ThemeModel.toAppTheme(): AppTheme = appTheme
