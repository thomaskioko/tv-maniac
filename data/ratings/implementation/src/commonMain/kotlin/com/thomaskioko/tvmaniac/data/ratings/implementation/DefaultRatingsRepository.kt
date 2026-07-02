package com.thomaskioko.tvmaniac.data.ratings.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.toDbProvider
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRatingEntry
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsDao
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRating
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRatingEntry
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRatingEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRatingsRepository(
    private val ratingsDao: RatingsDao,
    private val tvShowsDao: TvShowsDao,
    private val providerMetaDao: ProviderMetaDao,
    private val ratingsStore: RatingsStore,
    private val activeSource: () -> RatingsRemoteDataSource?,
    private val appScopeLauncher: AppScopeLauncher,
    private val syncObserver: SyncObserver,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : RatingsRepository {

    private val syncMutex = Mutex()

    override suspend fun refreshCommunityRating(showId: Long) {
        ratingsStore.fresh(showId) { logger.debug(TAG, it) }
    }

    override fun observeShowRating(showId: Long): Flow<ShowRating> {
        val communityFlow = activeSource()?.provider?.toDbProvider()
            ?.let { providerMetaDao.observeProviderRating(showId, it) }
            ?: flowOf(null)

        return combine(ratingsDao.observeShowRating(showId), communityFlow) { userEntry, community ->
            ShowRating(
                userRating = userEntry?.userRating?.toInt(),
                communityRating = community?.rating,
                communityVotes = community?.voteCount,
                pendingAction = userEntry?.pendingAction ?: PendingAction.NOTHING,
            )
        }
    }

    override suspend fun rateShow(showId: Long, rating: Int) {
        ratingsDao.upsertShowUserRating(
            showId = showId,
            userRating = rating.toLong(),
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override suspend fun removeShowRating(showId: Long) {
        ratingsDao.clearShowUserRating(showId)

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override fun observeSeasonRating(seasonId: Long): Flow<SeasonRating> =
        ratingsDao.observeSeasonRating(seasonId).map { entry ->
            SeasonRating(
                userRating = entry?.userRating?.toInt(),
                pendingAction = entry?.pendingAction ?: PendingAction.NOTHING,
            )
        }

    override suspend fun rateSeason(seasonId: Long, rating: Int) {
        ratingsDao.upsertSeasonUserRating(
            seasonId = seasonId,
            userRating = rating.toLong(),
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override suspend fun removeSeasonRating(seasonId: Long) {
        ratingsDao.clearSeasonUserRating(seasonId)

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override fun observeEpisodeRating(episodeId: Long): Flow<EpisodeRating> =
        ratingsDao.observeEpisodeRating(episodeId).map { entry ->
            EpisodeRating(
                userRating = entry?.userRating?.toInt(),
                pendingAction = entry?.pendingAction ?: PendingAction.NOTHING,
            )
        }

    override suspend fun rateEpisode(episodeId: Long, rating: Int) {
        ratingsDao.upsertEpisodeUserRating(
            episodeId = episodeId,
            userRating = rating.toLong(),
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override suspend fun removeEpisodeRating(episodeId: Long) {
        ratingsDao.clearEpisodeUserRating(episodeId)

        appScopeLauncher.launch(TAG) { syncPendingRatings() }
    }

    override suspend fun syncPendingRatings() {
        syncMutex.withLock { processPendingUploadActions() }
    }

    private suspend fun processPendingUploadActions() {
        try {
            val source = activeSource() ?: return
            if (pushShowUploads(source) == PendingRatingsOutcome.BACK_OFF) return
            if (pushShowDeletes(source) == PendingRatingsOutcome.BACK_OFF) return
            if (pushSeasonUploads(source) == PendingRatingsOutcome.BACK_OFF) return
            if (pushSeasonDeletes(source) == PendingRatingsOutcome.BACK_OFF) return
            if (pushEpisodeUploads(source) == PendingRatingsOutcome.BACK_OFF) return
            pushEpisodeDeletes(source)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
        }
    }

    private suspend fun pushShowUploads(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.showRatingsWithUploadPendingAction()) {
            val tmdbId = tvShowsDao.getTmdbIdForLocalShowId(entry.showId) ?: continue
            val userRating = entry.userRating?.toInt() ?: continue

            when (val response = source.addShowRating(tmdbId, userRating)) {
                is ApiResponse.Success -> ratingsDao.updateShowRatingPendingAction(entry.showId, PendingAction.NOTHING)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logShowSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private suspend fun pushShowDeletes(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.showRatingsWithDeletePendingAction()) {
            val tmdbId = tvShowsDao.getTmdbIdForLocalShowId(entry.showId) ?: continue

            when (val response = source.removeShowRating(tmdbId)) {
                is ApiResponse.Success -> ratingsDao.deleteShowRating(entry.showId)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logShowSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private suspend fun pushSeasonUploads(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.seasonRatingsWithUploadPendingAction()) {
            val userRating = entry.userRating?.toInt() ?: continue

            when (val response = source.addSeasonRating(entry.seasonId, userRating)) {
                is ApiResponse.Success -> ratingsDao.updateSeasonRatingPendingAction(entry.seasonId, PendingAction.NOTHING)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logSeasonSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private suspend fun pushSeasonDeletes(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.seasonRatingsWithDeletePendingAction()) {
            when (val response = source.removeSeasonRating(entry.seasonId)) {
                is ApiResponse.Success -> ratingsDao.deleteSeasonRating(entry.seasonId)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logSeasonSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private suspend fun pushEpisodeUploads(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.episodeRatingsWithUploadPendingAction()) {
            val userRating = entry.userRating?.toInt() ?: continue

            when (val response = source.addEpisodeRating(entry.episodeId, userRating)) {
                is ApiResponse.Success -> ratingsDao.updateEpisodeRatingPendingAction(entry.episodeId, PendingAction.NOTHING)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logEpisodeSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private suspend fun pushEpisodeDeletes(source: RatingsRemoteDataSource): PendingRatingsOutcome {
        for (entry in ratingsDao.episodeRatingsWithDeletePendingAction()) {
            when (val response = source.removeEpisodeRating(entry.episodeId)) {
                is ApiResponse.Success -> ratingsDao.deleteEpisodeRating(entry.episodeId)
                is ApiResponse.Unauthenticated -> return PendingRatingsOutcome.BACK_OFF
                is ApiResponse.Error -> logEpisodeSyncFailure(entry, response)
            }
        }
        return PendingRatingsOutcome.CONTINUE
    }

    private fun logShowSyncFailure(entry: ShowRatingEntry, error: ApiResponse.Error<*>) {
        syncObserver.log(
            SyncError.BackgroundSyncFailed(
                operationId = TAG,
                cause = error.toThrowable("show ${entry.showId}"),
            ),
        )
    }

    private fun logSeasonSyncFailure(entry: SeasonRatingEntry, error: ApiResponse.Error<*>) {
        syncObserver.log(
            SyncError.BackgroundSyncFailed(
                operationId = TAG,
                cause = error.toThrowable("season ${entry.seasonId}"),
            ),
        )
    }

    private fun logEpisodeSyncFailure(entry: EpisodeRatingEntry, error: ApiResponse.Error<*>) {
        syncObserver.log(
            SyncError.BackgroundSyncFailed(
                operationId = TAG,
                cause = error.toThrowable("episode ${entry.episodeId}"),
            ),
        )
    }

    private fun ApiResponse.Error<*>.toThrowable(subject: String): Throwable = when (this) {
        is ApiResponse.Error.HttpError -> Throwable("HTTP $code for $subject: $errorMessage")
        is ApiResponse.Error.SerializationError -> Throwable("Serialization error for $subject: $errorMessage")
        is ApiResponse.Error.NetworkFailure -> Throwable("Network failure for $subject: $kind", cause)
        is ApiResponse.Error.OfflineError -> Throwable(errorMessage)
    }

    private enum class PendingRatingsOutcome { CONTINUE, BACK_OFF }

    private companion object {
        private const val TAG = "ratings_sync"
    }
}
