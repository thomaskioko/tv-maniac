package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import com.thomaskioko.tvmaniac.data.backup.api.BackupRepository
import com.thomaskioko.tvmaniac.data.backup.api.RestoredListWriter
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupEpisode
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupEpisodeRating
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupList
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupListShow
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupPreferences
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupRating
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupResult
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupSeason
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupSeasonRating
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupShow
import com.thomaskioko.tvmaniac.data.backup.api.model.BackupWatchedEpisode
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreFailure
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.api.model.RestoreSummary
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
    private val destination: BackupDestination,
    private val syncObserver: SyncObserver,
    private val restoredListWriter: RestoredListWriter,
    transactionRunner: DatabaseTransactionRunner,
) : BackupRepository {

    private val queries = database.backupQueries
    private val importer = BackupImporter(database, transactionRunner)

    override suspend fun createBackup(): BackupFile = BackupFile(
        version = BackupFormat.VERSION,
        createdAt = dateTimeProvider.now().toString(),
        appVersion = appMetadata.versionName,
        shows = readShows(),
        lists = readLists(),
        preferences = readPreferences(),
    )

    override suspend fun writeBackup(folder: String, fileName: String): BackupResult {
        val backup = createBackup()
        val contents = BackupJson.encode(backup)

        val written = try {
            destination.write(folder, fileName, contents)
        } catch (unreadable: BackupLocationUnreadableException) {
            return BackupResult.Failed(BackupFailure.LocationUnavailable, unreadable)
        } catch (error: Throwable) {
            return BackupResult.Failed(BackupFailure.WriteFailed, error)
        }

        val verified = try {
            BackupJson.decode(destination.read(written))
        } catch (error: Throwable) {
            return BackupResult.Failed(BackupFailure.VerificationFailed, error)
        }

        if (verified != backup) {
            return BackupResult.Failed(BackupFailure.VerificationFailed)
        }

        return BackupResult.Success(
            showCount = backup.shows.size,
            episodeCount = backup.shows.sumOf { it.watchedEpisodes.size },
        )
    }

    override suspend fun restoreBackup(location: String, syncWithConnectedAccount: Boolean): RestoreResult {
        if (syncObserver.isSyncing.value) return RestoreResult.Failed(RestoreFailure.SyncInProgress)

        val backup = try {
            BackupJson.decode(destination.read(location))
        } catch (error: BackupVersionTooNewException) {
            return RestoreResult.Failed(RestoreFailure.VersionTooNew, error)
        } catch (error: Throwable) {
            return RestoreResult.Failed(RestoreFailure.ReadFailed, error)
        }

        return syncObserver.trackSync(RESTORE_OPERATION) {
            val safetyCopy = writeBackup(destination.safetyCopyFolder(), BackupFormat.SAFETY_COPY_NAME)
            if (safetyCopy is BackupResult.Failed) {
                return@trackSync RestoreResult.Failed(RestoreFailure.SafetyCopyFailed, safetyCopy.cause)
            }

            try {
                val includeSpecials = datastoreRepository.getIncludeSpecials()
                val summary = withContext(dispatchers.databaseWrite) {
                    importer.import(
                        backup = backup,
                        includeSpecials = includeSpecials,
                        syncWithConnectedAccount = syncWithConnectedAccount,
                    )
                }
                writePreferences(backup.preferences)
                RestoreResult.Restored(summary.withRestoredLists(restoreLists(backup, syncWithConnectedAccount)))
            } catch (error: Throwable) {
                RestoreResult.Failed(RestoreFailure.ImportFailed, error)
            }
        }
    }

    private suspend fun restoreLists(backup: BackupFile, syncWithConnectedAccount: Boolean): Int {
        if (!syncWithConnectedAccount) return 0
        return restoredListWriter.restoreLists(backup.lists)
    }

    override suspend fun showsNeedingMetadata(): List<Long> = withContext(dispatchers.databaseRead) {
        database.restoreQueries.showsNeedingMetadata().executeAsList().map { it.id }
    }

    private suspend fun readLists(): List<BackupList> = withContext(dispatchers.databaseRead) {
        val showsByList = queries.backupListShows().executeAsList()
            .groupBy({ it.list_id }) { BackupListShow(tmdbId = it.tmdb_id.id, listedAt = it.listed_at) }

        queries.backupLists().executeAsList().map { list ->
            BackupList(
                name = list.name,
                description = list.description,
                createdAt = list.created_at,
                shows = showsByList[list.id].orEmpty(),
            )
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

        val episodesBySeason = queries.backupEpisodes().executeAsList().groupBy { it.season_id.id }
        val seasonsByShow = queries.backupSeasons().executeAsList()
            .groupBy({ it.tmdb_id.id }) { season ->
                BackupSeason(
                    tmdbId = season.id.id,
                    seasonNumber = season.season_number,
                    title = season.title,
                    episodeCount = season.episode_count,
                    overview = season.overview,
                    imageUrl = season.image_url,
                    episodes = episodesBySeason[season.id.id].orEmpty().map { episode ->
                        BackupEpisode(
                            tmdbId = episode.id.id,
                            episodeNumber = episode.episode_number,
                            title = episode.title,
                            overview = episode.overview,
                            runtime = episode.runtime,
                            voteCount = episode.vote_count,
                            ratings = episode.ratings,
                            imageUrl = episode.image_url,
                            firstAired = episode.first_aired,
                        )
                    },
                )
            }

        queries.backupShows().executeAsList().map { show ->
            val tmdbId = show.tmdb_id.id
            BackupShow(
                tmdbId = tmdbId,
                title = show.name,
                overview = show.overview,
                posterPath = show.poster_path,
                backdropPath = show.backdrop_path,
                year = show.year,
                language = show.language,
                status = show.status,
                runtime = show.runtime,
                ratings = show.ratings,
                voteCount = show.vote_count,
                genres = show.genres.orEmpty(),
                seasonNumbers = show.season_numbers,
                episodeNumbers = show.episode_numbers,
                seasons = seasonsByShow[tmdbId].orEmpty(),
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

private fun RestoreSummary.withRestoredLists(restored: Int): RestoreSummary = copy(
    listsRestored = restored,
    listsNotRestored = listsNotRestored - restored,
)
