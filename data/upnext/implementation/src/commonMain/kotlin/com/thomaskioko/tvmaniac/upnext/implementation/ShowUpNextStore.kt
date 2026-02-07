package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.NEXT_EPISODES_SYNC
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class ShowUpNextStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val upNextDao: UpNextDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<NextEpisodeWithShow>> by storeBuilder(
    fetcher = apiFetcher { traktRemoteDataSource.getWatchedProgress(it) },
    sourceOfTruth = SourceOfTruth.of(
        reader = { upNextDao.observeNextEpisodeForShow(it) },
        writer = { showTraktId: Long, response: TraktWatchedProgressResponse ->
            withContext(dispatchers.databaseWrite) {
                val nextEpisode = response.nextEpisode
                val lastEpisode = response.lastEpisode
                val traktLastWatchedAt = response.lastWatchedAt?.let { dateTimeProvider.isoDateToEpoch(it) }

                upNextDao.upsert(
                    showTraktId = showTraktId,
                    episodeTraktId = nextEpisode?.ids?.trakt?.toLong(),
                    seasonNumber = nextEpisode?.seasonNumber?.toLong(),
                    episodeNumber = nextEpisode?.episodeNumber?.toLong(),
                    title = nextEpisode?.title,
                    overview = nextEpisode?.overview,
                    runtime = nextEpisode?.runtime?.toLong(),
                    firstAired = nextEpisode?.firstAired?.let { dateTimeProvider.isoDateToEpoch(it) },
                    imageUrl = null,
                    isShowComplete = nextEpisode == null,
                    lastEpisodeSeason = lastEpisode?.seasonNumber?.toLong(),
                    lastEpisodeNumber = lastEpisode?.episodeNumber?.toLong(),
                    traktLastWatchedAt = traktLastWatchedAt,
                    updatedAt = dateTimeProvider.nowMillis(),
                )

                upNextDao.upsertShowProgress(
                    showTraktId = showTraktId,
                    watchedCount = response.completed.toLong(),
                    totalCount = response.aired.toLong(),
                )

                requestManagerRepository.upsert(
                    entityId = showTraktId,
                    requestType = NEXT_EPISODES_SYNC.name,
                )
            }
        },
        deleteAll = { upNextDao.deleteAll() },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { episodesList ->
        episodesList.firstOrNull()?.showTraktId?.let { showTraktId ->
            withContext(dispatchers.io) {
                !requestManagerRepository.isRequestExpired(
                    entityId = showTraktId,
                    requestType = NEXT_EPISODES_SYNC.name,
                    threshold = NEXT_EPISODES_SYNC.duration,
                )
            }
        } ?: false
    },
).build()
