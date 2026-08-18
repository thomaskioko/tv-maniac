package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestinationBuilder
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
import com.thomaskioko.tvmaniac.data.backup.api.RestoreFailure
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
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
    private val destinationBuilder: BackupDestinationBuilder,
    private val syncObserver: SyncObserver,
    transactionRunner: DatabaseTransactionRunner,
) : BackupRepository {

    private val queries = database.backupQueries
    private val importer = BackupImporter(database, transactionRunner)

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

    override suspend fun restoreBackup(source: BackupDestination): RestoreResult {
        if (syncObserver.isSyncing.value) return RestoreResult.Failed(RestoreFailure.SyncInProgress)

        val backup = try {
            BackupJson.decode(source.read())
        } catch (error: BackupVersionTooNewException) {
            return RestoreResult.Failed(RestoreFailure.VersionTooNew, error)
        } catch (error: Throwable) {
            return RestoreResult.Failed(RestoreFailure.ReadFailed, error)
        }

        return syncObserver.trackSync(RESTORE_OPERATION) {
            val safetyCopy = writeBackup(destinationBuilder.safetyCopy())
            if (safetyCopy is BackupResult.Failed) {
                return@trackSync RestoreResult.Failed(RestoreFailure.SafetyCopyFailed, safetyCopy.cause)
            }

            try {
                val includeSpecials = datastoreRepository.getIncludeSpecials()
                val summary = withContext(dispatchers.databaseWrite) {
                    importer.import(backup, includeSpecials)
                }
                writePreferences(backup.preferences)
                RestoreResult.Restored(summary)
            } catch (error: Throwable) {
                RestoreResult.Failed(RestoreFailure.ImportFailed, error)
            }
        }
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

    private suspend fun writePreferences(preferences: BackupPreferences) {
        enumOrNull<AppTheme>(preferences.theme)?.let { datastoreRepository.saveTheme(it) }
        preferences.language?.let { datastoreRepository.saveLanguage(it) }
        enumOrNull<ListStyle>(preferences.listStyle)?.let { datastoreRepository.saveListStyle(it) }
        enumOrNull<ImageQuality>(preferences.imageQuality)?.let { datastoreRepository.saveImageQuality(it) }
        preferences.openTrailersInYoutube?.let { datastoreRepository.saveOpenTrailersInYoutube(it) }
        preferences.includeSpecials?.let { datastoreRepository.saveIncludeSpecials(it) }
        preferences.backgroundSyncEnabled?.let { datastoreRepository.setBackgroundSyncEnabled(it) }
        preferences.episodeNotificationsEnabled?.let { datastoreRepository.setEpisodeNotificationsEnabled(it) }
        preferences.librarySortOption?.let { datastoreRepository.saveLibrarySortOption(it) }
        preferences.upNextSortOption?.let { datastoreRepository.saveUpNextSortOption(it) }
        preferences.watchlistSortOption?.let { datastoreRepository.saveWatchlistSortOption(it) }
        preferences.genreShowCategory?.let { datastoreRepository.saveGenreShowCategory(it) }
        preferences.crashReportingEnabled?.let { datastoreRepository.setCrashReportingEnabled(it) }
        preferences.hapticFeedbackEnabled?.let { datastoreRepository.saveHapticFeedbackEnabled(it) }
        enumOrNull<SeasonSortOrder>(preferences.seasonSortOrder)?.let { datastoreRepository.saveSeasonSortOrder(it) }
        preferences.blurUnwatchedEpisodeImages?.let { datastoreRepository.saveBlurUnwatchedEpisodeImages(it) }
        datastoreRepository.saveHiddenDiscoverSections(
            preferences.hiddenDiscoverSections.mapNotNull { enumOrNull<DiscoverSection>(it) }.toSet(),
        )
        preferences.fontSizePercent?.let { datastoreRepository.saveFontSizePercent(it) }
        enumOrNull<PosterWidth>(preferences.posterWidth)?.let { datastoreRepository.savePosterWidth(it) }
        enumOrNull<PosterWidth>(preferences.landscapeWidth)?.let { datastoreRepository.saveLandscapeWidth(it) }
        enumOrNull<PosterCornerStyle>(preferences.posterCornerStyle)?.let { datastoreRepository.savePosterCornerStyle(it) }
        preferences.quickRateEnabled?.let { datastoreRepository.saveQuickRateEnabled(it) }
        preferences.multiplePlaysEnabled?.let { datastoreRepository.saveMultiplePlaysEnabled(it) }
    }

    private inline fun <reified T : Enum<T>> enumOrNull(name: String?): T? =
        enumValues<T>().firstOrNull { it.name == name }

    private companion object {
        private const val RESTORE_OPERATION = "backup-restore"
    }
}
