package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

public interface DatastoreRepository {
    /**
     * Saves the application's theme preference.
     *
     * @param appTheme The [AppTheme] to be saved, which can be one of LIGHT_THEME, DARK_THEME, or SYSTEM_THEME.
     */
    public fun saveTheme(appTheme: AppTheme)

    /**
     * Observes changes to the application's theme preference stored in the datastore.
     *
     * @return A Flow emitting the current [AppTheme], which can be LIGHT_THEME, DARK_THEME, or SYSTEM_THEME.
     */
    public fun observeTheme(): Flow<AppTheme>

    /**
     * Saves the user's preferred language.
     *
     * @param languageCode The ISO 639-1 language code (e.g., "en", "fr", "de").
     */
    public suspend fun saveLanguage(languageCode: String)

    /**
     * Observes the user's preferred language.
     *
     * @return A Flow of the user's preferred language code.
     */
    public fun observeLanguage(): Flow<String>

    /**
     * Saves the user's preferred list style for the watchlist.
     *
     * @param listStyle The [ListStyle] to be saved (GRID or LIST).
     */
    public suspend fun saveListStyle(listStyle: ListStyle)

    /**
     * Observes the user's preferred list style for the watchlist.
     *
     * @return A Flow of the user's preferred [ListStyle], defaulting to GRID.
     */
    public fun observeListStyle(): Flow<ListStyle>

    /**
     * Saves the user's preferred image quality setting.
     *
     * @param quality The [ImageQuality] to be saved (HIGH, MEDIUM, or LOW).
     */
    public suspend fun saveImageQuality(quality: ImageQuality)

    /**
     * Observes the user's preferred image quality setting.
     *
     * @return A Flow of the current [ImageQuality], defaulting to MEDIUM.
     */
    public fun observeImageQuality(): Flow<ImageQuality>

    /**
     * Saves the user's preference for opening trailers in the YouTube app.
     *
     * @param enabled Whether trailers should open in the YouTube app.
     */
    public suspend fun saveOpenTrailersInYoutube(enabled: Boolean)

    /**
     * Observes the user's preference for opening trailers in the YouTube app.
     *
     * @return A Flow of Boolean, true if trailers should open in YouTube app, false otherwise.
     */
    public fun observeOpenTrailersInYoutube(): Flow<Boolean>

    /**
     * Saves the user's preference for including Season 0 (Specials) when marking seasons as watched.
     *
     * @param includeSpecials Whether to include Specials episodes when marking seasons.
     */
    public suspend fun saveIncludeSpecials(includeSpecials: Boolean)

    /**
     * Observes the user's preference for including Specials when marking seasons as watched.
     *
     * @return A Flow of Boolean, true if Specials should be included, false otherwise. Defaults to false.
     */
    public fun observeIncludeSpecials(): Flow<Boolean>

    /**
     * Saves the last logged-in Trakt user ID (slug).
     * Used to detect user switches on re-login and prevent data leakage between accounts.
     *
     * @param userId The Trakt user slug, or null to clear.
     */
    public suspend fun saveLastTraktUserId(userId: String?)

    /**
     * Gets the last logged-in Trakt user ID (slug).
     *
     * @return The stored user slug, or null if none.
     */
    public suspend fun getLastTraktUserId(): String?
}
