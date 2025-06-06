package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.Language
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
     * Gets the list of supported locales.
     *
     * @return A list of supported language codes.
     */
    public fun getSupportedLocales(): Flow<List<String>>

    /**
     * Gets a Language object from a language code.
     *
     * @param code The ISO 639-1 language code (e.g., "en", "fr", "es").
     * @return A Language object with the code and its display name.
     */
    public suspend fun getLanguageFromCode(code: String): Language
}
