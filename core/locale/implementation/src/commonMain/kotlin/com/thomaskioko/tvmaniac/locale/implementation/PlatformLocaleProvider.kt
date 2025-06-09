package com.thomaskioko.tvmaniac.locale.implementation

import kotlinx.coroutines.flow.Flow

public expect class PlatformLocaleProvider {
    /**
     * Gets the current locale code.
     *
     * @return The current locale code (e.g., "en", "fr", "es").
     */
    public fun getCurrentLocale(): Flow<String>

    /**
     * Sets the locale to the specified language code.
     *
     * @param languageCode The ISO 639-1 language code (e.g., "en", "fr", "es").
     */
    public suspend fun setLocale(languageCode: String)

    /**
     * Gets the list of the preferred locales.
     *
     * @return A list of supported language codes.
     */
    public fun getPreferredLocales(): Flow<List<String>>
}
