package com.thomaskioko.tvmaniac.datastore.testing

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeDatastoreRepository : DatastoreRepository {

    private val appThemeFlow = MutableStateFlow(AppTheme.SYSTEM_THEME)
    private val languageFlow = MutableStateFlow("en")
    private val listStyleFlow = MutableStateFlow(ListStyle.GRID)
    private val imageQualityFlow = MutableStateFlow(ImageQuality.AUTO)
    private val openTrailersInYoutubeFlow = MutableStateFlow(false)
    private val includeSpecialsFlow = MutableStateFlow(false)
    private val lastTraktUserId: MutableStateFlow<String?> = MutableStateFlow(null)
    private val backgroundSyncEnabledFlow = MutableStateFlow(true)
    private val lastSyncTimestampFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val episodeNotificationsEnabledFlow = MutableStateFlow(false)
    private val notificationPermissionAskedFlow = MutableStateFlow(true)
    private val showNotificationRationaleFlow = MutableStateFlow(false)
    private val requestNotificationPermissionFlow = MutableStateFlow(false)
    private val librarySortOptionFlow = MutableStateFlow("ADDED_DESC")

    public suspend fun setTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    public suspend fun setLastTraktUserId(userId: String?) {
        lastTraktUserId.value = userId
    }

    /**
     * Non-suspending opt-in setter for tests that need to override the permission-asked default
     * before the activity launches. The interface counterpart [setNotificationPermissionAsked] is
     * suspend, which deadlocks under `runBlocking` when the Compose Robolectric harness installs a
     * `TestDispatcher` as `Dispatchers.Main` (every dispatcher role binds to Main via
     * `IntegrationTestDispatcherBindings`). Use this from `@Before` to seed state synchronously.
     */
    public fun setNotificationPermissionAskedNow(asked: Boolean) {
        notificationPermissionAskedFlow.value = asked
    }

    public suspend fun setLanguage(languageCode: String) {
        languageFlow.value = languageCode
    }

    override fun saveTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    override fun observeTheme(): Flow<AppTheme> = appThemeFlow.asStateFlow()

    override suspend fun saveLanguage(languageCode: String) {
        languageFlow.value = languageCode
    }

    override fun observeLanguage(): Flow<String> = languageFlow.asStateFlow()

    override suspend fun saveListStyle(listStyle: ListStyle) {
        listStyleFlow.value = listStyle
    }

    override fun observeListStyle(): Flow<ListStyle> = listStyleFlow.asStateFlow()

    override suspend fun saveImageQuality(quality: ImageQuality) {
        imageQualityFlow.value = quality
    }

    override fun observeImageQuality(): Flow<ImageQuality> = imageQualityFlow.asStateFlow()

    override suspend fun saveOpenTrailersInYoutube(enabled: Boolean) {
        openTrailersInYoutubeFlow.value = enabled
    }

    override fun observeOpenTrailersInYoutube(): Flow<Boolean> = openTrailersInYoutubeFlow.asStateFlow()

    override suspend fun saveIncludeSpecials(includeSpecials: Boolean) {
        includeSpecialsFlow.value = includeSpecials
    }

    override fun observeIncludeSpecials(): Flow<Boolean> = includeSpecialsFlow.asStateFlow()

    override suspend fun getIncludeSpecials(): Boolean = includeSpecialsFlow.value

    override suspend fun saveLastTraktUserId(userId: String?) {
    }

    override suspend fun getLastTraktUserId(): String? = lastTraktUserId.value

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        backgroundSyncEnabledFlow.value = enabled
    }

    override fun observeBackgroundSyncEnabled(): Flow<Boolean> = backgroundSyncEnabledFlow.asStateFlow()

    override suspend fun setLastSyncTimestamp(timestamp: Long) {
        lastSyncTimestampFlow.value = timestamp
    }

    override fun observeLastSyncTimestamp(): Flow<Long?> = lastSyncTimestampFlow.asStateFlow()

    override suspend fun setEpisodeNotificationsEnabled(enabled: Boolean) {
        episodeNotificationsEnabledFlow.value = enabled
    }

    override fun observeEpisodeNotificationsEnabled(): Flow<Boolean> = episodeNotificationsEnabledFlow.asStateFlow()

    override suspend fun getEpisodeNotificationsEnabled(): Boolean = episodeNotificationsEnabledFlow.value

    override suspend fun setNotificationPermissionAsked(asked: Boolean) {
        notificationPermissionAskedFlow.value = asked
    }

    override fun observeNotificationPermissionAsked(): Flow<Boolean> = notificationPermissionAskedFlow.asStateFlow()

    override suspend fun getNotificationPermissionAsked(): Boolean = notificationPermissionAskedFlow.value

    override suspend fun setShowNotificationRationale(show: Boolean) {
        showNotificationRationaleFlow.value = show
    }

    override fun observeShowNotificationRationale(): Flow<Boolean> = showNotificationRationaleFlow.asStateFlow()

    override suspend fun setRequestNotificationPermission(request: Boolean) {
        requestNotificationPermissionFlow.value = request
    }

    override fun observeRequestNotificationPermission(): Flow<Boolean> = requestNotificationPermissionFlow.asStateFlow()

    override suspend fun saveLibrarySortOption(sortOption: String) {
        librarySortOptionFlow.value = sortOption
    }

    override fun observeLibrarySortOption(): Flow<String> = librarySortOptionFlow.asStateFlow()

    private val genreShowCategoryFlow = MutableStateFlow("POPULAR")
    private val upNextSortOptionFlow = MutableStateFlow("LAST_WATCHED")
    private val watchlistSortOptionFlow = MutableStateFlow("ADDED_DESC")
    private val lastUpNextSyncTimestampFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val lastTokenRefreshTimestampFlow: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val crashReportingEnabledFlow = MutableStateFlow(true)
    private val debugMenuEnabledFlow = MutableStateFlow(false)
    private val accountTypeFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val hapticFeedbackEnabledFlow = MutableStateFlow(true)
    private val seasonSortOrderFlow = MutableStateFlow(SeasonSortOrder.OLDEST_FIRST)
    private val blurUnwatchedEpisodeImagesFlow = MutableStateFlow(false)
    private val hiddenDiscoverSectionsFlow = MutableStateFlow<Set<DiscoverSection>>(emptySet())
    private val fontSizePercentFlow = MutableStateFlow(100)
    private val posterWidthFlow = MutableStateFlow(PosterWidth.STANDARD)
    private val landscapeWidthFlow = MutableStateFlow(PosterWidth.STANDARD)
    private val posterCornerStyleFlow = MutableStateFlow(PosterCornerStyle.SHARP)

    override suspend fun saveGenreShowCategory(category: String) {
        genreShowCategoryFlow.value = category
    }

    override suspend fun getGenreShowCategory(): String = genreShowCategoryFlow.value

    override fun observeGenreShowCategory(): Flow<String> = genreShowCategoryFlow.asStateFlow()

    override suspend fun saveUpNextSortOption(sortOption: String) {
        upNextSortOptionFlow.value = sortOption
    }

    override fun observeUpNextSortOption(): Flow<String> = upNextSortOptionFlow.asStateFlow()

    override suspend fun saveWatchlistSortOption(sortOption: String) {
        watchlistSortOptionFlow.value = sortOption
    }

    override fun observeWatchlistSortOption(): Flow<String> = watchlistSortOptionFlow.asStateFlow()

    override suspend fun setLastUpNextSyncTimestamp(timestamp: Long) {
        lastUpNextSyncTimestampFlow.value = timestamp
    }

    override fun observeLastUpNextSyncTimestamp(): Flow<Long?> = lastUpNextSyncTimestampFlow.asStateFlow()

    override suspend fun setLastTokenRefreshTimestamp(timestamp: Long) {
        lastTokenRefreshTimestampFlow.value = timestamp
    }

    override fun observeLastTokenRefreshTimestamp(): Flow<Long?> = lastTokenRefreshTimestampFlow.asStateFlow()

    override suspend fun setCrashReportingEnabled(enabled: Boolean) {
        crashReportingEnabledFlow.value = enabled
    }

    override fun observeCrashReportingEnabled(): Flow<Boolean> = crashReportingEnabledFlow.asStateFlow()

    override suspend fun setDebugMenuEnabled(enabled: Boolean) {
        debugMenuEnabledFlow.value = enabled
    }

    override fun observeDebugMenuEnabled(): Flow<Boolean> = debugMenuEnabledFlow.asStateFlow()

    override suspend fun saveAccountType(override: String?) {
        accountTypeFlow.value = override
    }

    override fun observeAccountType(): Flow<String?> = accountTypeFlow.asStateFlow()

    override suspend fun saveHapticFeedbackEnabled(enabled: Boolean) {
        hapticFeedbackEnabledFlow.value = enabled
    }

    override fun observeHapticFeedbackEnabled(): Flow<Boolean> = hapticFeedbackEnabledFlow.asStateFlow()

    override suspend fun saveSeasonSortOrder(sortOrder: SeasonSortOrder) {
        seasonSortOrderFlow.value = sortOrder
    }

    override fun observeSeasonSortOrder(): Flow<SeasonSortOrder> = seasonSortOrderFlow.asStateFlow()

    override suspend fun saveBlurUnwatchedEpisodeImages(enabled: Boolean) {
        blurUnwatchedEpisodeImagesFlow.value = enabled
    }

    override fun observeBlurUnwatchedEpisodeImages(): Flow<Boolean> = blurUnwatchedEpisodeImagesFlow.asStateFlow()

    override suspend fun saveHiddenDiscoverSections(sections: Set<DiscoverSection>) {
        hiddenDiscoverSectionsFlow.value = sections
    }

    override fun observeHiddenDiscoverSections(): Flow<Set<DiscoverSection>> = hiddenDiscoverSectionsFlow.asStateFlow()

    override suspend fun updateDiscoverSectionVisibility(section: DiscoverSection, visible: Boolean) {
        val current = hiddenDiscoverSectionsFlow.value
        hiddenDiscoverSectionsFlow.value = if (visible) current - section else current + section
    }

    override suspend fun saveFontSizePercent(percent: Int) {
        fontSizePercentFlow.value = percent
    }

    override fun observeFontSizePercent(): Flow<Int> = fontSizePercentFlow.asStateFlow()

    override suspend fun savePosterWidth(width: PosterWidth) {
        posterWidthFlow.value = width
    }

    override fun observePosterWidth(): Flow<PosterWidth> = posterWidthFlow.asStateFlow()

    override suspend fun saveLandscapeWidth(width: PosterWidth) {
        landscapeWidthFlow.value = width
    }

    override fun observeLandscapeWidth(): Flow<PosterWidth> = landscapeWidthFlow.asStateFlow()

    override suspend fun savePosterCornerStyle(style: PosterCornerStyle) {
        posterCornerStyleFlow.value = style
    }

    override fun observePosterCornerStyle(): Flow<PosterCornerStyle> = posterCornerStyleFlow.asStateFlow()
}
