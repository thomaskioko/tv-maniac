package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    /**
     * Saves the application's theme preference.
     *
     * @param appTheme The [AppTheme] to be saved, which can be one of LIGHT_THEME, DARK_THEME, or SYSTEM_THEME.
     */
    fun saveTheme(appTheme: AppTheme)

    /**
     * Observes changes to the application's theme preference stored in the datastore.
     *
     * @return A Flow emitting the current [AppTheme], which can be LIGHT_THEME, DARK_THEME, or SYSTEM_THEME.
     */
    fun observeTheme(): Flow<AppTheme>

    /**
     * Clears the stored authentication state from the datastore.
     *
     * This method removes any saved authentication details, such as access tokens,
     * refresh tokens, and the authorized state. It is typically used during logout
     * or when resetting authentication data.
     */
    fun clearAuthState()

    /**
     * Observes changes to the authentication state stored in the datastore.
     *
     * @return A Flow emitting the current [AuthState], which includes information such as
     * access token, refresh token, and authorization status.
     */
    fun observeAuthState(): Flow<AuthState>

    /**
     * Saves the provided authentication state to the datastore.
     *
     * @param authState The [AuthState] containing authentication details such as access token,
     * refresh token, and authorization status to be saved.
     */
    suspend fun saveAuthState(authState: AuthState)

    /**
     * Retrieves the current authentication state from the datastore.
     *
     * @return The current [AuthState], or null if no authentication state is stored.
     */
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

    /**
     * Saves the user's preferred list style for the watchlist.
     *
     * @param listStyle The [ListStyle] to be saved (GRID or LIST).
     */
    suspend fun saveListStyle(listStyle: ListStyle)

    /**
     * Observes the user's preferred list style for the watchlist.
     *
     * @return A Flow of the user's preferred [ListStyle], defaulting to GRID.
     */
    fun observeListStyle(): Flow<ListStyle>

    /**
     * Saves the user's preferred image quality setting.
     *
     * @param quality The [ImageQuality] to be saved (HIGH, MEDIUM, or LOW).
     */
    suspend fun saveImageQuality(quality: ImageQuality)

    /**
     * Observes the user's preferred image quality setting.
     *
     * @return A Flow of the current [ImageQuality], defaulting to MEDIUM.
     */
    fun observeImageQuality(): Flow<ImageQuality>
}
