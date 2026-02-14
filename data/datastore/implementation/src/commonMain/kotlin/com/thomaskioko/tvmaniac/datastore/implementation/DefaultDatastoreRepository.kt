package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultDatastoreRepository(
    private val coroutineScope: AppCoroutineScope,
    private val dataStore: DataStore<Preferences>,
) : DatastoreRepository {

    override fun saveTheme(appTheme: AppTheme) {
        coroutineScope.io.launch {
            dataStore.edit { preferences -> preferences[KEY_THEME] = appTheme.name }
        }
    }

    override fun observeTheme(): Flow<AppTheme> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_THEME]) {
                AppTheme.LIGHT_THEME.name -> AppTheme.LIGHT_THEME
                AppTheme.DARK_THEME.name -> AppTheme.DARK_THEME
                AppTheme.TERMINAL_THEME.name -> AppTheme.TERMINAL_THEME
                AppTheme.AUTUMN_THEME.name -> AppTheme.AUTUMN_THEME
                AppTheme.AQUA_THEME.name -> AppTheme.AQUA_THEME
                AppTheme.AMBER_THEME.name -> AppTheme.AMBER_THEME
                AppTheme.SNOW_THEME.name -> AppTheme.SNOW_THEME
                AppTheme.CRIMSON_THEME.name -> AppTheme.CRIMSON_THEME
                else -> AppTheme.SYSTEM_THEME
            }
        }

    override suspend fun saveLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = languageCode
        }
    }

    override fun observeLanguage(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[KEY_LANGUAGE] ?: "en"
        }

    override suspend fun saveListStyle(listStyle: ListStyle) {
        dataStore.edit { preferences ->
            preferences[KEY_LIST_STYLE] = listStyle.name
        }
    }

    override fun observeListStyle(): Flow<ListStyle> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_LIST_STYLE]) {
                ListStyle.LIST.name -> ListStyle.LIST
                else -> ListStyle.GRID // Default to GRID
            }
        }

    override suspend fun saveImageQuality(quality: ImageQuality) {
        dataStore.edit { preferences ->
            preferences[KEY_IMAGE_QUALITY] = quality.name
        }
    }

    override fun observeImageQuality(): Flow<ImageQuality> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_IMAGE_QUALITY]) {
                ImageQuality.AUTO.name -> ImageQuality.AUTO
                ImageQuality.HIGH.name -> ImageQuality.HIGH
                ImageQuality.MEDIUM.name -> ImageQuality.MEDIUM
                ImageQuality.LOW.name -> ImageQuality.LOW
                else -> ImageQuality.AUTO
            }
        }

    override suspend fun saveOpenTrailersInYoutube(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_OPEN_TRAILERS_IN_YOUTUBE] = enabled
        }
    }

    override fun observeOpenTrailersInYoutube(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_OPEN_TRAILERS_IN_YOUTUBE] ?: false
        }

    override suspend fun saveIncludeSpecials(includeSpecials: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_INCLUDE_SPECIALS] = includeSpecials
        }
    }

    override fun observeIncludeSpecials(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_INCLUDE_SPECIALS] ?: false
        }

    override suspend fun getIncludeSpecials(): Boolean =
        dataStore.data.first()[KEY_INCLUDE_SPECIALS] ?: false

    override suspend fun saveLastTraktUserId(userId: String?) {
        dataStore.edit { preferences ->
            if (userId != null) {
                preferences[KEY_LAST_TRAKT_USER_ID] = userId
            } else {
                preferences.remove(KEY_LAST_TRAKT_USER_ID)
            }
        }
    }

    override suspend fun getLastTraktUserId(): String? {
        return dataStore.data.first()[KEY_LAST_TRAKT_USER_ID]
    }

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_BACKGROUND_SYNC_ENABLED] = enabled
        }
    }

    override fun observeBackgroundSyncEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_BACKGROUND_SYNC_ENABLED] ?: true
        }

    override suspend fun setLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    override fun observeLastSyncTimestamp(): Flow<Long?> =
        dataStore.data.map { preferences ->
            preferences[KEY_LAST_SYNC_TIMESTAMP]
        }

    override suspend fun setEpisodeNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_EPISODE_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override fun observeEpisodeNotificationsEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_EPISODE_NOTIFICATIONS_ENABLED] ?: false
        }

    override suspend fun getEpisodeNotificationsEnabled(): Boolean =
        dataStore.data.first()[KEY_EPISODE_NOTIFICATIONS_ENABLED] ?: false

    override suspend fun setNotificationPermissionAsked(asked: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_PERMISSION_ASKED] = asked
        }
    }

    override fun observeNotificationPermissionAsked(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_NOTIFICATION_PERMISSION_ASKED] ?: false
        }

    override suspend fun getNotificationPermissionAsked(): Boolean =
        dataStore.data.first()[KEY_NOTIFICATION_PERMISSION_ASKED] ?: false

    override suspend fun setShowNotificationRationale(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_NOTIFICATION_RATIONALE] = show
        }
    }

    override fun observeShowNotificationRationale(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_SHOW_NOTIFICATION_RATIONALE] ?: false
        }

    override suspend fun setRequestNotificationPermission(request: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_REQUEST_NOTIFICATION_PERMISSION] = request
        }
    }

    override fun observeRequestNotificationPermission(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_REQUEST_NOTIFICATION_PERMISSION] ?: false
        }

    override suspend fun saveLibrarySortOption(sortOption: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LIBRARY_SORT_OPTION] = sortOption
        }
    }

    override fun observeLibrarySortOption(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[KEY_LIBRARY_SORT_OPTION] ?: "ADDED_DESC"
        }

    override suspend fun saveUpNextSortOption(sortOption: String) {
        dataStore.edit { preferences ->
            preferences[KEY_UPNEXT_SORT_OPTION] = sortOption
        }
    }

    override fun observeUpNextSortOption(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[KEY_UPNEXT_SORT_OPTION] ?: "LAST_WATCHED"
        }

    override suspend fun setLastUpNextSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_UPNEXT_SYNC_TIMESTAMP] = timestamp
        }
    }

    override fun observeLastUpNextSyncTimestamp(): Flow<Long?> =
        dataStore.data.map { preferences ->
            preferences[KEY_LAST_UPNEXT_SYNC_TIMESTAMP]
        }

    public companion object {
        public val KEY_THEME: Preferences.Key<String> = stringPreferencesKey("app_theme")
        public val KEY_LANGUAGE: Preferences.Key<String> = stringPreferencesKey("app_language")
        public val KEY_LIST_STYLE: Preferences.Key<String> = stringPreferencesKey("list_style")
        public val KEY_IMAGE_QUALITY: Preferences.Key<String> = stringPreferencesKey("image_quality")
        public val KEY_OPEN_TRAILERS_IN_YOUTUBE: Preferences.Key<Boolean> = booleanPreferencesKey("open_trailers_in_youtube")
        public val KEY_INCLUDE_SPECIALS: Preferences.Key<Boolean> = booleanPreferencesKey("include_specials")
        public val KEY_LAST_TRAKT_USER_ID: Preferences.Key<String> = stringPreferencesKey("last_trakt_user_id")
        public val KEY_BACKGROUND_SYNC_ENABLED: Preferences.Key<Boolean> = booleanPreferencesKey("background_sync_enabled")
        public val KEY_LAST_SYNC_TIMESTAMP: Preferences.Key<Long> = longPreferencesKey("last_sync_timestamp")
        public val KEY_EPISODE_NOTIFICATIONS_ENABLED: Preferences.Key<Boolean> = booleanPreferencesKey("episode_notifications_enabled")
        public val KEY_NOTIFICATION_PERMISSION_ASKED: Preferences.Key<Boolean> = booleanPreferencesKey("notification_permission_asked")
        public val KEY_SHOW_NOTIFICATION_RATIONALE: Preferences.Key<Boolean> = booleanPreferencesKey("show_notification_rationale")
        public val KEY_REQUEST_NOTIFICATION_PERMISSION: Preferences.Key<Boolean> = booleanPreferencesKey("request_notification_permission")
        public val KEY_LIBRARY_SORT_OPTION: Preferences.Key<String> = stringPreferencesKey("library_sort_option")
        public val KEY_UPNEXT_SORT_OPTION: Preferences.Key<String> = stringPreferencesKey("upnext_sort_option")
        public val KEY_LAST_UPNEXT_SYNC_TIMESTAMP: Preferences.Key<Long> = longPreferencesKey("last_upnext_sync_timestamp")
    }
}
