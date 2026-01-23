package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAILERS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class TrailerStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tvShowsDao: TvShowsDao,
    private val trailerDao: TrailerDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<SelectByShowTraktId>> by storeBuilder(
    fetcher = Fetcher.of { traktId: Long ->
        traktRemoteDataSource.getShowVideos(traktId).getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of<Long, List<TraktVideosResponse>, List<SelectByShowTraktId>>(
        reader = { traktId: Long ->
            trailerDao.observeTrailersByShowTraktId(traktId)
        },
        writer = { traktId, videos ->
            databaseTransactionRunner {
                val tmdbId = tvShowsDao.getTmdbIdByTraktId(traktId)
                    ?: return@databaseTransactionRunner

                videos
                    .filter { it.site.equals("YouTube", ignoreCase = true) }
                    .forEach { video ->
                        val youtubeKey = extractYouTubeKey(video.url) ?: return@forEach
                        trailerDao.upsert(
                            Trailers(
                                id = youtubeKey,
                                show_tmdb_id = Id(tmdbId),
                                youtube_url = video.url,
                                name = video.title,
                                site = video.site,
                                size = video.size?.toLong() ?: 0L,
                                type = video.type,
                            ),
                        )
                    }

                requestManagerRepository.upsert(
                    entityId = traktId,
                    requestType = TRAILERS.name,
                )
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            val traktId = cachedData.firstOrNull()?.show_trakt_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = traktId,
                requestType = TRAILERS.name,
                threshold = TRAILERS.duration,
            )
        }
    },
).build()

private fun extractYouTubeKey(url: String): String? {
    return when {
        url.contains("youtube.com/watch") -> url.substringAfter("v=").substringBefore("&")
        url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
        else -> null
    }
}
