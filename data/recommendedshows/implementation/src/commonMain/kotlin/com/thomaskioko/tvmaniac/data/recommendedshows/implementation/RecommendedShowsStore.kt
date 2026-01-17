package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsParams
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.RECOMMENDED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
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
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class RecommendedShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val recommendedShowsDao: RecommendedShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<RecommendedShowsParams, List<RecommendedShows>> by storeBuilder(
    fetcher = Fetcher.of { param: RecommendedShowsParams ->
        coroutineScope {
            traktRemoteDataSource.getRelatedShows(
                traktId = param.traktId,
                page = param.page.toInt(),
            ).getOrThrow()
                .mapNotNull { show ->
                    val tmdbId = show.ids.tmdb ?: return@mapNotNull null
                    async {
                        val tmdbResult = runCatching {
                            tmdbDataSource.getShowDetails(tmdbId)
                        }
                        RecommendedShowResult(
                            traktShow = show,
                            tmdbId = tmdbId,
                            tmdbDetails = tmdbResult.getOrNull()?.let {
                                (it as? ApiResponse.Success)?.body
                            },
                        )
                    }
                }
                .awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of<RecommendedShowsParams, List<RecommendedShowResult>, List<RecommendedShows>>(
        reader = { param: RecommendedShowsParams -> recommendedShowsDao.observeRecommendedShows(param.traktId) },
        writer = { param: RecommendedShowsParams, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    response.forEachIndexed { _, result ->
                        val traktId = result.traktShow.ids.trakt
                        val tmdbId = result.tmdbId

                        tvShowsDao.upsertMerging(result.toTvshow(traktId, tmdbId, formatterUtil))

                        recommendedShowsDao.upsert(
                            showTraktId = traktId,
                            showTmdbId = tmdbId,
                            recommendedShowTraktId = param.traktId,
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = param.traktId,
                    requestType = RECOMMENDED_SHOWS.name,
                )
            }
        },
        delete = { param -> recommendedShowsDao.delete(param.traktId) },
        deleteAll = recommendedShowsDao::deleteAll,
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            val showTraktId = cachedData.firstOrNull()?.show_trakt_id?.id ?: return@withContext false
            !requestManagerRepository.isRequestExpired(
                entityId = showTraktId,
                requestType = RECOMMENDED_SHOWS.name,
                threshold = RECOMMENDED_SHOWS.duration,
            )
        }
    },
).build()

private fun RecommendedShowResult.toTvshow(traktId: Long, tmdbId: Long, formatterUtil: FormatterUtil): Tvshow {
    val tmdb = tmdbDetails
    val trakt = traktShow
    return Tvshow(
        trakt_id = Id(traktId),
        tmdb_id = Id(tmdbId),
        name = tmdb?.name ?: trakt.title,
        overview = tmdb?.overview ?: trakt.overview ?: "",
        language = tmdb?.originalLanguage ?: trakt.language,
        year = tmdb?.firstAirDate ?: trakt.firstAirDate,
        ratings = tmdb?.voteAverage ?: trakt.rating ?: 0.0,
        vote_count = tmdb?.voteCount?.toLong() ?: trakt.votes?.toLong() ?: 0L,
        poster_path = tmdb?.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdrop_path = tmdb?.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        status = tmdb?.status ?: trakt.status,
        genres = trakt.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
        episode_numbers = trakt.airedEpisodes?.toString(),
        season_numbers = null,
    )
}
