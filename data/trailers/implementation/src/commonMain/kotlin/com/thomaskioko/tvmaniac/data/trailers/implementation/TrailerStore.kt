package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrNull
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAILERS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.VideoResultResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

private const val YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v="

@Inject
public class TrailerStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbShowDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val showIdResolver: ShowIdResolver,
    private val trailerDao: TrailerDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<SelectByShowId>> by storeBuilder(
    fetcher = Fetcher.of { tmdbShowId: Long ->
        val traktId = tvShowsDao.getTraktIdByTmdbId(tmdbShowId)
        val trailers: List<TrailerEntry> = if (traktId != null) {
            traktRemoteDataSource.getShowVideos(traktId).getOrThrow()
                .filter { it.site.equals("YouTube", ignoreCase = true) }
                .mapNotNull { video ->
                    val key = extractYouTubeKey(video.url) ?: return@mapNotNull null
                    TrailerEntry(
                        id = key,
                        youtubeUrl = video.url,
                        name = video.title,
                        site = video.site,
                        size = video.size?.toLong() ?: 0L,
                        type = video.type,
                    )
                }
        } else {
            tmdbShowDetailsDataSource.getShowDetails(tmdbShowId).getOrNull()
                ?.videos
                ?.results
                ?.filter { it.site.equals("YouTube", ignoreCase = true) }
                ?.map { it.toTrailerEntry() }
                .orEmpty()
        }
        requestManagerRepository.upsert(
            entityId = tmdbShowId,
            requestType = TRAILERS.name,
        )
        trailers
    },
    sourceOfTruth = SourceOfTruth.of<Long, List<TrailerEntry>, List<SelectByShowId>>(
        reader = { tmdbShowId: Long ->
            trailerDao.observeTrailersByShowId(tmdbShowId)
        },
        writer = { tmdbShowId, trailers ->
            databaseTransactionRunner {
                val internalShowId = showIdResolver.showIdForTmdbId(tmdbShowId)
                    ?: return@databaseTransactionRunner

                trailers.forEach { entry ->
                    trailerDao.upsert(
                        Trailers(
                            id = entry.id,
                            show_id = internalShowId,
                            youtube_url = entry.youtubeUrl,
                            name = entry.name,
                            site = entry.site,
                            size = entry.size,
                            type = entry.type,
                        ),
                    )
                }
            }
        },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { result ->
        withContext(dispatchers.io) {
            val showId = result.firstOrNull()?.show_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showId,
                requestType = TRAILERS.name,
                threshold = TRAILERS.duration,
            )
        }
    },
).build()

private data class TrailerEntry(
    val id: String,
    val youtubeUrl: String,
    val name: String,
    val site: String,
    val size: Long,
    val type: String,
)

private fun VideoResultResponse.toTrailerEntry(): TrailerEntry = TrailerEntry(
    id = key,
    youtubeUrl = "$YOUTUBE_BASE_URL$key",
    name = name,
    site = site,
    size = size.toLong(),
    type = type,
)

private fun extractYouTubeKey(url: String): String? {
    return when {
        url.contains("youtube.com/watch") -> url.substringAfter("v=").substringBefore("&")
        url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
        else -> null
    }
}
