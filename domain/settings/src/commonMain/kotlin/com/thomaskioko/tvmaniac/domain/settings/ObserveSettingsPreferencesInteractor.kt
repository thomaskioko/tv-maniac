package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveSettingsPreferencesInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<Unit, SettingsPreferences>() {

    override fun createObservable(params: Unit): Flow<SettingsPreferences> {
        return combine(
            datastoreRepository.observeImageQuality(),
            datastoreRepository.observeTheme(),
            datastoreRepository.observeOpenTrailersInYoutube(),
            datastoreRepository.observeIncludeSpecials(),
            datastoreRepository.observeBackgroundSyncEnabled(),
            datastoreRepository.observeLastSyncTimestamp(),
            datastoreRepository.observeEpisodeNotificationsEnabled(),
        ) { imageQuality, theme, openTrailersInYoutube, includeSpecials, backgroundSyncEnabled,
            lastSyncTimestamp, episodeNotificationsEnabled,
            ->
            val lastSyncDate = lastSyncTimestamp?.let { dateTimeProvider.epochToDisplayDateTime(it) }
            SettingsPreferences(
                imageQuality = imageQuality,
                theme = theme,
                openTrailersInYoutube = openTrailersInYoutube,
                includeSpecials = includeSpecials,
                backgroundSyncEnabled = backgroundSyncEnabled,
                lastSyncDate = lastSyncDate,
                showLastSyncDate = backgroundSyncEnabled && lastSyncDate != null,
                episodeNotificationsEnabled = episodeNotificationsEnabled,
            )
        }
    }
}

public data class SettingsPreferences(
    val imageQuality: ImageQuality,
    val theme: AppTheme,
    val openTrailersInYoutube: Boolean,
    val includeSpecials: Boolean,
    val backgroundSyncEnabled: Boolean,
    val lastSyncDate: String?,
    val showLastSyncDate: Boolean,
    val episodeNotificationsEnabled: Boolean,
)
