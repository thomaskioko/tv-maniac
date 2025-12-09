package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.i18n.StringResourceKey

enum class ThemeModel(
    val id: String,
    val displayNameKey: StringResourceKey,
    val descriptionKey: StringResourceKey,
    val isDark: Boolean,
    val displayOrder: Int,
) {
    SYSTEM(
        id = "system",
        displayNameKey = StringResourceKey.SettingsThemeSystem,
        descriptionKey = StringResourceKey.SettingsThemeSystemDescription,
        isDark = false,
        displayOrder = 0,
    ),
    LIGHT(
        id = "light",
        displayNameKey = StringResourceKey.SettingsThemeLight,
        descriptionKey = StringResourceKey.SettingsThemeLightDescription,
        isDark = false,
        displayOrder = 1,
    ),
    DARK(
        id = "dark",
        displayNameKey = StringResourceKey.SettingsThemeDark,
        descriptionKey = StringResourceKey.SettingsThemeDarkDescription,
        isDark = true,
        displayOrder = 2,
    ),
    TERMINAL(
        id = "terminal",
        displayNameKey = StringResourceKey.SettingsThemeTerminal,
        descriptionKey = StringResourceKey.SettingsThemeTerminalDescription,
        isDark = true,
        displayOrder = 3,
    ),
    AUTUMN(
        id = "autumn",
        displayNameKey = StringResourceKey.SettingsThemeAutumn,
        descriptionKey = StringResourceKey.SettingsThemeAutumnDescription,
        isDark = false,
        displayOrder = 4,
    ),
    AQUA(
        id = "aqua",
        displayNameKey = StringResourceKey.SettingsThemeAqua,
        descriptionKey = StringResourceKey.SettingsThemeAquaDescription,
        isDark = true,
        displayOrder = 5,
    ),
    ;

    companion object {
        val sortedByDisplayOrder: List<ThemeModel> = entries.sortedBy { it.displayOrder }
    }
}

fun AppTheme.toThemeModel(): ThemeModel = when (this) {
    AppTheme.SYSTEM_THEME -> ThemeModel.SYSTEM
    AppTheme.LIGHT_THEME -> ThemeModel.LIGHT
    AppTheme.DARK_THEME -> ThemeModel.DARK
    AppTheme.TERMINAL_THEME -> ThemeModel.TERMINAL
    AppTheme.AUTUMN_THEME -> ThemeModel.AUTUMN
    AppTheme.AQUA_THEME -> ThemeModel.AQUA
}

fun ThemeModel.toAppTheme(): AppTheme = when (this) {
    ThemeModel.SYSTEM -> AppTheme.SYSTEM_THEME
    ThemeModel.LIGHT -> AppTheme.LIGHT_THEME
    ThemeModel.DARK -> AppTheme.DARK_THEME
    ThemeModel.TERMINAL -> AppTheme.TERMINAL_THEME
    ThemeModel.AUTUMN -> AppTheme.AUTUMN_THEME
    ThemeModel.AQUA -> AppTheme.AQUA_THEME
}
