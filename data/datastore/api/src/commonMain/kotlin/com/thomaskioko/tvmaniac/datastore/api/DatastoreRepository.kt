package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun saveTheme(appTheme: AppTheme)

    fun observeTheme(): Flow<AppTheme>

    fun clearAuthState()

    fun observeAuthState(): Flow<AuthState>

    suspend fun saveAuthState(authState: AuthState)

    suspend fun getAuthState(): AuthState?

    /**
     * Saves the user's preferred language.
     *
     * @param languageCode The ISO 639-1 language code (e.g., "en", "fr", "de").
     */
    suspend fun saveLanguage(languageCode: String)

    /**
     * Observes the user's preferred language.
     *
     * @return A Flow of the user's preferred language code.
     */
    fun observeLanguage(): Flow<String>
}

enum class AppTheme(val value: String) {
    LIGHT_THEME("Light Theme"),
    DARK_THEME("Light Theme"),
    SYSTEM_THEME("Light Theme"),
}
