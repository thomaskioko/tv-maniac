package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrNull
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsParams
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.RECOMMENDED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
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
public class RecommendedShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val tvShowsDao: TvShowsDao,
    private val recommendedShowsDao: RecommendedShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<RecommendedShowsParams, List<RecommendedShows>> by storeBuilder(
    fetcher = Fetcher.of { param: RecommendedShowsParams ->
        coroutineScope {
            val traktId = tvShowsDao.getTraktIdByTmdbId(param.showId)
            if (traktId != null) {
                traktRemoteDataSource.getRelatedShows(
                    showId = traktId,
                    page = param.page.toInt(),
                ).getOrThrow()
                    .mapNotNull { show ->
                        val tmdbId = show.ids.tmdb ?: return@mapNotNull null
                        async {
                            val tmdbDetails = tmdbDataSource.getShowDetails(tmdbId).getOrNull()
                            RecommendedShowResult(
                                tmdbId = tmdbId,
                                traktShow = show,
                                tmdbDetails = tmdbDetails,
                            )
                        }
                    }
                    .awaitAll()
            } else {
                tmdbDataSource.getRecommendedShows(
                    id = param.showId,
                    page = param.page,
                ).getOrNull()
                    ?.results
                    ?.map { show ->
                        RecommendedShowResult(
                            tmdbId = show.id.toLong(),
                            tmdbShow = show,
                        )
                    }
                    .orEmpty()
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of<RecommendedShowsParams, List<RecommendedShowResult>, List<RecommendedShows>>(
        reader = { param: RecommendedShowsParams ->
            val traktId = tvShowsDao.getTraktIdByTmdbId(param.showId) ?: param.showId
            recommendedShowsDao.observeRecommendedShows(traktId)
        },
        writer = { param: RecommendedShowsParams, response ->
            withContext(dispatchers.databaseWrite) {
                val parentId = tvShowsDao.getTraktIdByTmdbId(param.showId) ?: param.showId
                databaseTransactionRunner {
                    response.forEach { result ->
                        val tmdbId = result.tmdbId

                        tvShowsDao.upsertMerging(result.toShowToPersist(formatterUtil, dateTimeProvider))

                        recommendedShowsDao.upsert(
                            showId = tmdbId,
                            tmdbId = tmdbId,
                            traktId = parentId,
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = param.showId,
                    requestType = RECOMMENDED_SHOWS.name,
                )
            }
        },
        delete = { param ->
            val traktId = tvShowsDao.getTraktIdByTmdbId(param.showId) ?: param.showId
            recommendedShowsDao.delete(traktId)
        },
        deleteAll = recommendedShowsDao::deleteAll,
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
                requestType = RECOMMENDED_SHOWS.name,
                threshold = RECOMMENDED_SHOWS.duration,
            )
        }
    },
).build()

private fun RecommendedShowResult.toShowToPersist(
    formatterUtil: FormatterUtil,
    dateTimeProvider: DateTimeProvider,
): ShowToPersist {
    val tmdbDetails = tmdbDetails
    val tmdbShow = tmdbShow
    val trakt = traktShow
    val dateString = tmdbDetails?.firstAirDate ?: tmdbShow?.firstAirDate ?: trakt?.firstAirDate
    return ShowToPersist(
        showId = trakt?.ids?.trakt?.let { Id(it) },
        tmdbId = Id(tmdbId),
        name = tmdbDetails?.name ?: tmdbShow?.name ?: trakt?.title ?: "",
        overview = tmdbDetails?.overview ?: tmdbShow?.overview ?: trakt?.overview ?: "",
        language = tmdbDetails?.originalLanguage ?: tmdbShow?.originalLanguage ?: trakt?.language,
        year = dateString?.let { dateTimeProvider.extractYear(it) },
        ratings = tmdbDetails?.voteAverage ?: tmdbShow?.voteAverage ?: trakt?.rating ?: 0.0,
        voteCount = tmdbDetails?.voteCount?.toLong() ?: tmdbShow?.voteCount?.toLong() ?: trakt?.votes ?: 0L,
        posterPath = (tmdbDetails?.posterPath ?: tmdbShow?.posterPath)?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdropPath = (tmdbDetails?.backdropPath ?: tmdbShow?.backdropPath)?.let { formatterUtil.formatTmdbPosterPath(it) },
        status = tmdbDetails?.status ?: trakt?.status,
        genres = trakt?.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
        episodeNumbers = trakt?.airedEpisodes?.toString(),
        seasonNumbers = null,
    )
}
