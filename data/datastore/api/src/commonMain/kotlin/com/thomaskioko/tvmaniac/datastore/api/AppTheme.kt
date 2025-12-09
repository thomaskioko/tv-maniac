package com.thomaskioko.tvmaniac.datastore.api

import com.thomaskioko.tvmaniac.i18n.StringResourceKey

enum class AppTheme(
    val displayNameKey: StringResourceKey,
    val descriptionKey: StringResourceKey,
    val isDark: Boolean,
    val displayOrder: Int,
) {
    SYSTEM_THEME(
        displayNameKey = StringResourceKey.SettingsThemeSystem,
        descriptionKey = StringResourceKey.SettingsThemeSystemDescription,
        isDark = false,
        displayOrder = 0,
    ),
    LIGHT_THEME(
        displayNameKey = StringResourceKey.SettingsThemeLight,
        descriptionKey = StringResourceKey.SettingsThemeLightDescription,
        isDark = false,
        displayOrder = 1,
    ),
    DARK_THEME(
        displayNameKey = StringResourceKey.SettingsThemeDark,
        descriptionKey = StringResourceKey.SettingsThemeDarkDescription,
        isDark = true,
        displayOrder = 2,
    ),
    TERMINAL_THEME(
        displayNameKey = StringResourceKey.SettingsThemeTerminal,
        descriptionKey = StringResourceKey.SettingsThemeTerminalDescription,
        isDark = true,
        displayOrder = 3,
    ),
    AUTUMN_THEME(
        displayNameKey = StringResourceKey.SettingsThemeAutumn,
        descriptionKey = StringResourceKey.SettingsThemeAutumnDescription,
        isDark = false,
        displayOrder = 4,
    ),
    AQUA_THEME(
        displayNameKey = StringResourceKey.SettingsThemeAqua,
        descriptionKey = StringResourceKey.SettingsThemeAquaDescription,
        isDark = true,
        displayOrder = 5,
    ),
}
