package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class SimilarShowStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val similarShowsDao: SimilarShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SimilarParams, List<SimilarShows>> by storeBuilder(
    fetcher = Fetcher.of { param: SimilarParams ->
        coroutineScope {
            val traktShow = traktRemoteDataSource.getShowByTmdbId(param.showId).getOrThrow()
                .firstOrNull { it.type == "show" }?.show
                ?: error("Could not find Trakt ID for TMDB ID: ${param.showId}")

            traktRemoteDataSource.getRelatedShows(
                traktId = traktShow.ids.trakt.toLong(),
                page = param.page.toInt(),
            ).getOrThrow()
                .filter { it.ids.tmdb != null }
                .map { show ->
                    async {
                        val tmdbResult = runCatching {
                            tmdbDataSource.getShowDetails(show.ids.tmdb!!.toLong())
                        }
                        SimilarShowResult(
                            traktShow = show,
                            tmdbDetails = tmdbResult.getOrNull()?.let {
                                (it as? ApiResponse.Success)?.body
                            },
                        )
                    }
                }
                .awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of<SimilarParams, List<SimilarShowResult>, List<SimilarShows>>(
        reader = { param: SimilarParams -> similarShowsDao.observeSimilarShows(param.showId) },
        writer = { param: SimilarParams, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    response.forEachIndexed { index, result ->
                        val showId = result.traktShow.ids.tmdb!!.toLong()

                        if (!tvShowsDao.showExists(showId)) {
                            tvShowsDao.upsert(result.toTvshow(showId, formatterUtil))
                        }

                        similarShowsDao.upsert(
                            similarShowId = showId,
                            showId = param.showId,
                            pageOrder = index,
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = param.showId,
                    requestType = SIMILAR_SHOWS.name,
                )
            }
        },
        delete = { param -> similarShowsDao.delete(param.showId) },
        deleteAll = { databaseTransactionRunner(similarShowsDao::deleteAll) },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            val showId = cachedData.firstOrNull()?.show_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showId,
                requestType = SIMILAR_SHOWS.name,
                threshold = SIMILAR_SHOWS.duration,
            )
        }
    },
).build()

private data class SimilarShowResult(
    val traktShow: TraktShowResponse,
    val tmdbDetails: TmdbShowDetailsResponse?,
)

private fun SimilarShowResult.toTvshow(showId: Long, formatterUtil: FormatterUtil): Tvshow {
    val tmdb = tmdbDetails
    val trakt = traktShow
    return Tvshow(
        id = Id(showId),
        name = tmdb?.name ?: trakt.title,
        overview = tmdb?.overview ?: trakt.overview ?: "",
        language = tmdb?.originalLanguage ?: trakt.language,
        first_air_date = tmdb?.firstAirDate ?: trakt.firstAirDate,
        popularity = tmdb?.popularity ?: 0.0,
        vote_average = tmdb?.voteAverage ?: trakt.rating ?: 0.0,
        vote_count = tmdb?.voteCount?.toLong() ?: trakt.votes?.toLong() ?: 0L,
        poster_path = tmdb?.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdrop_path = tmdb?.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        status = tmdb?.status ?: trakt.status,
        genre_ids = tmdb?.genres?.map { it.id } ?: emptyList(),
        episode_numbers = null,
        last_air_date = null,
        season_numbers = null,
    )
}
