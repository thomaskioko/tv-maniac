package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupEpisodeRating
import com.thomaskioko.tvmaniac.data.backup.api.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupPreferences
import com.thomaskioko.tvmaniac.data.backup.api.BackupRating
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import com.thomaskioko.tvmaniac.data.backup.api.BackupSeasonRating
import com.thomaskioko.tvmaniac.data.backup.api.BackupShow
import com.thomaskioko.tvmaniac.data.backup.api.BackupWatchedEpisode
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultBackupRepository(
    private val database: TvManiacDatabase,
    private val datastoreRepository: DatastoreRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val appMetadata: AppMetadata,
    private val dispatchers: AppCoroutineDispatchers,
) : BackupRepository {

    private val queries = database.backupQueries

    override suspend fun createBackup(): BackupFile = BackupFile(
        version = BackupFormat.VERSION,
        createdAt = dateTimeProvider.now().toString(),
        appVersion = appMetadata.versionName,
        shows = readShows(),
        preferences = readPreferences(),
    )

    override suspend fun writeBackup(destination: BackupDestination): BackupResult {
        val backup = createBackup()
        val contents = BackupJson.encode(backup)

        try {
            destination.write(contents)
        } catch (error: Throwable) {
            return BackupResult.Failed(BackupFailure.WriteFailed, error)
        }

        val verified = try {
            BackupJson.decode(destination.read())
        } catch (error: Throwable) {
            return BackupResult.Failed(BackupFailure.VerificationFailed, error)
        }

        if (verified != backup) {
            return BackupResult.Failed(BackupFailure.VerificationFailed)
        }

        return BackupResult.Written(
            showCount = backup.shows.size,
            episodeCount = backup.shows.sumOf { it.watchedEpisodes.size },
        )
    }

    private suspend fun readShows(): List<BackupShow> = withContext(dispatchers.databaseRead) {
        val followedAt = queries.backupFollowedShows().executeAsList()
            .associate { it.tmdb_id.id to it.followed_at }
        val watchStatus = queries.backupWatchStatus().executeAsList()
            .associate { it.tmdb_id.id to it.status.name }
        val showRatings = queries.backupShowRatings().executeAsList()
            .associate { it.tmdb_id.id to BackupRating(value = it.user_rating, ratedAt = it.rated_at) }
        val watchedEpisodes = queries.backupWatchedEpisodes().executeAsList()
            .groupBy({ it.tmdb_id.id }) {
                BackupWatchedEpisode(
                    season = it.season_number,
                    episode = it.episode_number,
                    watchedAt = it.watched_at,
                )
            }
        val seasonRatings = queries.backupSeasonRatings().executeAsList()
            .groupBy({ it.tmdb_id.id }) {
                BackupSeasonRating(season = it.season_number, value = it.user_rating, ratedAt = it.rated_at)
            }
        val episodeRatings = queries.backupEpisodeRatings().executeAsList()
            .groupBy({ it.tmdb_id.id }) {
                BackupEpisodeRating(
                    season = it.season_number,
                    episode = it.episode_number,
                    value = it.user_rating,
                    ratedAt = it.rated_at,
                )
            }

        queries.backupShows().executeAsList().map { show ->
            val tmdbId = show.tmdb_id.id
            BackupShow(
                tmdbId = tmdbId,
                title = show.name,
                followedAt = followedAt[tmdbId],
                watchStatus = watchStatus[tmdbId],
                rating = showRatings[tmdbId],
                watchedEpisodes = watchedEpisodes[tmdbId].orEmpty(),
                seasonRatings = seasonRatings[tmdbId].orEmpty(),
                episodeRatings = episodeRatings[tmdbId].orEmpty(),
            )
        }
    }

    private suspend fun readPreferences(): BackupPreferences = BackupPreferences(
        theme = datastoreRepository.observeTheme().first().name,
        language = datastoreRepository.observeLanguage().first(),
        listStyle = datastoreRepository.observeListStyle().first().name,
        imageQuality = datastoreRepository.observeImageQuality().first().name,
        openTrailersInYoutube = datastoreRepository.observeOpenTrailersInYoutube().first(),
        includeSpecials = datastoreRepository.observeIncludeSpecials().first(),
        backgroundSyncEnabled = datastoreRepository.observeBackgroundSyncEnabled().first(),
        episodeNotificationsEnabled = datastoreRepository.observeEpisodeNotificationsEnabled().first(),
        librarySortOption = datastoreRepository.observeLibrarySortOption().first(),
        upNextSortOption = datastoreRepository.observeUpNextSortOption().first(),
        watchlistSortOption = datastoreRepository.observeWatchlistSortOption().first(),
        genreShowCategory = datastoreRepository.observeGenreShowCategory().first(),
        crashReportingEnabled = datastoreRepository.observeCrashReportingEnabled().first(),
        hapticFeedbackEnabled = datastoreRepository.observeHapticFeedbackEnabled().first(),
        seasonSortOrder = datastoreRepository.observeSeasonSortOrder().first().name,
        blurUnwatchedEpisodeImages = datastoreRepository.observeBlurUnwatchedEpisodeImages().first(),
        hiddenDiscoverSections = datastoreRepository.observeHiddenDiscoverSections().first().map { it.name },
        fontSizePercent = datastoreRepository.observeFontSizePercent().first(),
        posterWidth = datastoreRepository.observePosterWidth().first().name,
        landscapeWidth = datastoreRepository.observeLandscapeWidth().first().name,
        posterCornerStyle = datastoreRepository.observePosterCornerStyle().first().name,
        quickRateEnabled = datastoreRepository.observeQuickRateEnabled().first(),
        multiplePlaysEnabled = datastoreRepository.observeMultiplePlaysEnabled().first(),
    )
}
