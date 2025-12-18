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

    /**
     * Saves the user's preference for opening trailers in the YouTube app.
     *
     * @param enabled Whether trailers should open in the YouTube app.
     */
    suspend fun saveOpenTrailersInYoutube(enabled: Boolean)

    /**
     * Observes the user's preference for opening trailers in the YouTube app.
     *
     * @return A Flow of Boolean, true if trailers should open in YouTube app, false otherwise.
     */
    fun observeOpenTrailersInYoutube(): Flow<Boolean>

    /**
     * Saves the user's preference for including Season 0 (Specials) when marking seasons as watched.
     *
     * @param includeSpecials Whether to include Specials episodes when marking seasons.
     */
    suspend fun saveIncludeSpecials(includeSpecials: Boolean)

    /**
     * Observes the user's preference for including Specials when marking seasons as watched.
     *
     * @return A Flow of Boolean, true if Specials should be included, false otherwise. Defaults to false.
     */
    fun observeIncludeSpecials(): Flow<Boolean>

    /**
     * Saves the last logged-in Trakt user ID (slug).
     * Used to detect user switches on re-login and prevent data leakage between accounts.
     *
     * @param userId The Trakt user slug, or null to clear.
     */
    suspend fun saveLastTraktUserId(userId: String?)

    /**
     * Gets the last logged-in Trakt user ID (slug).
     *
     * @return The stored user slug, or null if none.
     */
    suspend fun getLastTraktUserId(): String?
}
