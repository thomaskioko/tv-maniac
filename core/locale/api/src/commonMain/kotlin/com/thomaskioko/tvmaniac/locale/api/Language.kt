package com.thomaskioko.tvmaniac.locale.api

/**
 * Represents a language with its code and display name.
 *
 * @property code The ISO 639-1 language code (e.g., "en", "fr", "es").
 * @property displayName The human-readable name of the language.
 */
public data class Language(
    val code: String,
    val displayName: String,
)
