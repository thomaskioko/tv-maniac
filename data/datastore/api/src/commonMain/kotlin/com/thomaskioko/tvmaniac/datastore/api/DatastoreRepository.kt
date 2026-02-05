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
     * Gets the user's preference for including Specials when marking seasons as watched.
     *
     * @return Boolean, true if Specials should be included, false otherwise. Defaults to false.
     */
    public suspend fun getIncludeSpecials(): Boolean

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

    /**
     * Saves the user's preference for background sync.
     *
     * @param enabled Whether background sync is enabled.
     */
    public suspend fun setBackgroundSyncEnabled(enabled: Boolean)

    /**
     * Observes the user's preference for background sync.
     *
     * @return A Flow of Boolean, true if background sync is enabled, false otherwise. Defaults to true.
     */
    public fun observeBackgroundSyncEnabled(): Flow<Boolean>

    /**
     * Saves the timestamp of the last successful background sync.
     *
     * @param timestamp The epoch milliseconds of the last sync.
     */
    public suspend fun setLastSyncTimestamp(timestamp: Long)

    /**
     * Observes the timestamp of the last successful background sync.
     *
     * @return A Flow of the timestamp in epoch milliseconds, or null if never synced.
     */
    public fun observeLastSyncTimestamp(): Flow<Long?>

    /**
     * Saves the user's preference for episode notifications.
     *
     * @param enabled Whether episode notifications are enabled.
     */
    public suspend fun setEpisodeNotificationsEnabled(enabled: Boolean)

    /**
     * Observes the user's preference for episode notifications.
     *
     * @return A Flow of Boolean, true if episode notifications are enabled, false otherwise. Defaults to false.
     */
    public fun observeEpisodeNotificationsEnabled(): Flow<Boolean>

    /**
     * Saves whether the notification permission has been asked.
     * Used to ensure we only prompt once on first launch.
     *
     * @param asked Whether the permission has been asked.
     */
    public suspend fun setNotificationPermissionAsked(asked: Boolean)

    /**
     * Observes whether the notification permission has been asked.
     *
     * @return A Flow of Boolean, true if permission has been asked, false otherwise. Defaults to false.
     */
    public fun observeNotificationPermissionAsked(): Flow<Boolean>

    /**
     * Sets whether to show the notification rationale dialog.
     *
     * @param show Whether to show the rationale dialog.
     */
    public suspend fun setShowNotificationRationale(show: Boolean)

    /**
     * Observes whether to show the notification rationale dialog.
     *
     * @return A Flow of Boolean, true if rationale should be shown.
     */
    public fun observeShowNotificationRationale(): Flow<Boolean>

    /**
     * Sets whether to request the system notification permission.
     *
     * @param request Whether to request the permission.
     */
    public suspend fun setRequestNotificationPermission(request: Boolean)

    /**
     * Observes whether to request the system notification permission.
     *
     * @return A Flow of Boolean, true if permission should be requested.
     */
    public fun observeRequestNotificationPermission(): Flow<Boolean>

    /**
     * Saves the user's preferred library sort option.
     *
     * @param sortOption The sort option name (e.g., "LAST_WATCHED", "ALPHABETICAL").
     */
    public suspend fun saveLibrarySortOption(sortOption: String)

    /**
     * Observes the user's preferred library sort option.
     *
     * @return A Flow of the sort option name, defaulting to "LAST_WATCHED_DESC".
     */
    public fun observeLibrarySortOption(): Flow<String>

    /**
     * Saves the user's preferred Up Next sort option.
     *
     * @param sortOption The sort option name (e.g., "LAST_WATCHED", "AIR_DATE").
     */
    public suspend fun saveUpNextSortOption(sortOption: String)

    /**
     * Observes the user's preferred Up Next sort option.
     *
     * @return A Flow of the sort option name, defaulting to "LAST_WATCHED".
     */
    public fun observeUpNextSortOption(): Flow<String>
}
