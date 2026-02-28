package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SIMILAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
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
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<SimilarParams, List<SimilarShows>> by storeBuilder(
    fetcher = Fetcher.of { param: SimilarParams ->
        coroutineScope {
            val results = traktRemoteDataSource.getRelatedShows(
                traktId = param.showTraktId,
                page = param.page.toInt(),
            ).getOrThrow()
                .mapNotNull { show ->
                    val tmdbId = show.ids.tmdb ?: return@mapNotNull null
                    async {
                        val tmdbResult = runCatching {
                            tmdbDataSource.getShowDetails(tmdbId)
                        }
                        SimilarShowResult(
                            traktShow = show,
                            tmdbId = tmdbId,
                            tmdbDetails = tmdbResult.getOrNull()?.let {
                                (it as? ApiResponse.Success)?.body
                            },
                        )
                    }
                }
                .awaitAll()

            requestManagerRepository.upsert(
                entityId = param.showTraktId,
                requestType = SIMILAR_SHOWS.name,
            )

            results
        }
    },
    sourceOfTruth = SourceOfTruth.of<SimilarParams, List<SimilarShowResult>, List<SimilarShows>>(
        reader = { param: SimilarParams -> similarShowsDao.observeSimilarShows(param.showTraktId) },
        writer = { param: SimilarParams, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    response.forEachIndexed { index, result ->
                        val traktId = result.traktShow.ids.trakt
                        val tmdbId = result.tmdbId

                        tvShowsDao.upsertMerging(result.toTvshow(traktId, tmdbId, formatterUtil, dateTimeProvider))

                        similarShowsDao.upsert(
                            showTraktId = traktId,
                            showTmdbId = tmdbId,
                            similarShowTraktId = param.showTraktId,
                            pageOrder = index,
                        )
                    }
                }
            }
        },
        delete = { param -> similarShowsDao.delete(param.showTraktId) },
        deleteAll = { databaseTransactionRunner(similarShowsDao::deleteAll) },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            val parentShowTraktId = cachedData.firstOrNull()?.similar_show_trakt_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = parentShowTraktId,
                requestType = SIMILAR_SHOWS.name,
                threshold = SIMILAR_SHOWS.duration,
            )
        }
    },
).build()

private fun SimilarShowResult.toTvshow(
    traktId: Long,
    tmdbId: Long,
    formatterUtil: FormatterUtil,
    dateTimeProvider: DateTimeProvider,
): Tvshow {
    val tmdb = tmdbDetails
    val trakt = traktShow
    val dateString = tmdb?.firstAirDate ?: trakt.firstAirDate
    return Tvshow(
        trakt_id = Id(traktId),
        tmdb_id = Id(tmdbId),
        name = tmdb?.name ?: trakt.title,
        overview = tmdb?.overview ?: trakt.overview ?: "",
        language = tmdb?.originalLanguage ?: trakt.language,
        year = dateString?.let { dateTimeProvider.extractYear(it) },
        ratings = tmdb?.voteAverage ?: trakt.rating ?: 0.0,
        vote_count = tmdb?.voteCount?.toLong() ?: trakt.votes ?: 0L,
        poster_path = tmdb?.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdrop_path = tmdb?.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        status = tmdb?.status ?: trakt.status,
        genres = trakt.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
        episode_numbers = trakt.airedEpisodes?.toString(),
        season_numbers = null,
    )
}
