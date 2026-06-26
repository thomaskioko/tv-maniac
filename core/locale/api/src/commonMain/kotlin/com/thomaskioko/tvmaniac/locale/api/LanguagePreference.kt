package com.thomaskioko.tvmaniac.locale.api

import kotlinx.coroutines.flow.Flow

public interface LanguagePreference {
    public fun observeLanguage(): Flow<String>

    public suspend fun saveLanguage(languageCode: String)
}
