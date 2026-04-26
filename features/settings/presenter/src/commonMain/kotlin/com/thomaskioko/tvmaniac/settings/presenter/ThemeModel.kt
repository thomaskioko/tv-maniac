package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.theme.Theme
import com.thomaskioko.tvmaniac.i18n.StringResourceKey

public enum class ThemeModel(public val theme: Theme) {
    SYSTEM(Theme.SYSTEM_THEME),
    LIGHT(Theme.LIGHT_THEME),
    DARK(Theme.DARK_THEME),
    TERMINAL(Theme.TERMINAL_THEME),
    AUTUMN(Theme.AUTUMN_THEME),
    AQUA(Theme.AQUA_THEME),
    AMBER(Theme.AMBER_THEME),
    SNOW(Theme.SNOW_THEME),
    CRIMSON(Theme.CRIMSON_THEME),
    ;

    public val displayNameKey: StringResourceKey
        get() = theme.displayNameKey
    public val displayOrder: Int
        get() = theme.displayOrder

    public companion object {
        public val sortedByDisplayOrder: List<ThemeModel> = entries.sortedBy { it.displayOrder }
    }
}

public fun AppTheme.toTheme(): Theme = Theme.valueOf(name)

public fun Theme.toAppTheme(): AppTheme = AppTheme.valueOf(name)

public fun Theme.toThemeModel(): ThemeModel =
    ThemeModel.entries.first { it.theme == this }

public fun ThemeModel.toTheme(): Theme = theme

public fun ThemeModel.toAppTheme(): AppTheme = theme.toAppTheme()
