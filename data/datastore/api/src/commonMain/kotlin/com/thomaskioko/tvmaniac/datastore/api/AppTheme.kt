package com.thomaskioko.tvmaniac.datastore.api

import com.thomaskioko.tvmaniac.i18n.StringResourceKey

public enum class AppTheme(
    public val displayNameKey: StringResourceKey,
    public val descriptionKey: StringResourceKey,
    public val isDark: Boolean,
    public val displayOrder: Int,
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
        displayOrder = 7,
    ),
    AUTUMN_THEME(
        displayNameKey = StringResourceKey.SettingsThemeAutumn,
        descriptionKey = StringResourceKey.SettingsThemeAutumnDescription,
        isDark = false,
        displayOrder = 3,
    ),
    AQUA_THEME(
        displayNameKey = StringResourceKey.SettingsThemeAqua,
        descriptionKey = StringResourceKey.SettingsThemeAquaDescription,
        isDark = true,
        displayOrder = 4,
    ),
    AMBER_THEME(
        displayNameKey = StringResourceKey.SettingsThemeAmber,
        descriptionKey = StringResourceKey.SettingsThemeAmberDescription,
        isDark = true,
        displayOrder = 5,
    ),
    SNOW_THEME(
        displayNameKey = StringResourceKey.SettingsThemeSnow,
        descriptionKey = StringResourceKey.SettingsThemeSnowDescription,
        isDark = true,
        displayOrder = 6,
    ),
    CRIMSON_THEME(
        displayNameKey = StringResourceKey.SettingsThemeCrimson,
        descriptionKey = StringResourceKey.SettingsThemeCrimsonDescription,
        isDark = true,
        displayOrder = 8,
    ),
}
