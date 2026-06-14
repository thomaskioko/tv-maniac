package com.thomaskioko.tvmaniac.startwatching.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.START_WATCHING_SYNC
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class StartWatchingWatchlistStore(
    private val activeSource: () -> StartWatchingRemoteDataSource?,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val followedShowsDao: FollowedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<FollowedShowEntry>> by storeBuilder(
    fetcher = Fetcher.of { _: Unit ->
        coroutineScope {
            val source = activeSource()
                ?: throw AuthenticationException("No active sync provider")
            source.getPlanToWatch()
                .getOrThrow()
                .mapNotNull { show ->
                    val tmdbId = show.tmdbId ?: return@mapNotNull null
                    tmdbId to show
                }
                .map { (tmdbId, show) ->
                    async {
                        when (val tmdb = tmdbDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> PlanToWatchWithImages(
                                show = show,
                                resolvedTmdbId = tmdbId,
                                tmdbPosterPath = tmdb.body.posterPath,
                                tmdbBackdropPath = tmdb.body.backdropPath,
                            )
                            is ApiResponse.Unauthenticated,
                            is ApiResponse.Error,
                            -> PlanToWatchWithImages(
                                show = show,
                                resolvedTmdbId = tmdbId,
                                tmdbPosterPath = null,
                                tmdbBackdropPath = null,
                            )
                        }
                    }
                }
                .awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> followedShowsDao.entriesObservable() },
        writer = { _: Unit, response: List<PlanToWatchWithImages> ->
            transactionRunner {
                val currentEntries = followedShowsDao.entriesWithNoPendingAction()
                val networkTmdbIds = response.map { it.resolvedTmdbId }.toSet()

                response.forEach { item ->
                    tvShowsDao.upsertMerging(
                        item.toShowToPersist(
                            posterPath = item.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            backdropPath = item.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        ),
                    )

                    val _ = followedShowsDao.upsert(item.toFollowedShowEntry())
                }

                currentEntries.forEach { localEntry ->
                    if (localEntry.showId !in networkTmdbIds) {
                        followedShowsDao.deleteById(localEntry.id)
                    }
                }
            }

            requestManagerRepository.upsert(
                entityId = START_WATCHING_SYNC.requestId,
                requestType = START_WATCHING_SYNC.name,
            )
        },
        delete = { _: Unit -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = START_WATCHING_SYNC.name,
                threshold = START_WATCHING_SYNC.duration,
            )
        }
    },
).build()

private data class PlanToWatchWithImages(
    val show: RemotePlanToWatchShow,
    val resolvedTmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)

private fun PlanToWatchWithImages.toFollowedShowEntry(): FollowedShowEntry = FollowedShowEntry(
    showId = resolvedTmdbId,
    tmdbId = resolvedTmdbId,
    followedAt = show.followedAt,
    pendingAction = PendingAction.NOTHING,
)

private fun PlanToWatchWithImages.toShowToPersist(
    posterPath: String?,
    backdropPath: String?,
): ShowToPersist = ShowToPersist(
    showId = null,
    tmdbId = Id<TmdbId>(resolvedTmdbId),
    name = show.title,
    overview = "",
    language = null,
    year = show.year?.toString(),
    status = null,
    ratings = 0.0,
    voteCount = 0,
    genres = null,
    posterPath = posterPath,
    backdropPath = backdropPath,
    episodeNumbers = null,
    seasonNumbers = null,
)
