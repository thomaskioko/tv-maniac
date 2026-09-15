package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.toDbProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SEARCH_RESULTS
import com.thomaskioko.tvmaniac.search.api.SearchDao
import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.traktGenreName
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import dev.zacsweers.metro.Provider as MetroProvider

@Inject
public class SearchShowStore(
    private val searchDao: SearchDao,
    private val tvShowsDao: TvShowsDao,
    private val activeSearchRemoteDataSource: MetroProvider<SearchRemoteDataSource>,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { key: String ->
        coroutineScope {
            val activeSource = activeSearchRemoteDataSource()
            val query = key.substringAfter(':')

            val remoteShows = activeSource.searchShows(query = query, limit = SEARCH_LIMIT).getOrThrow()
            val withTmdbId = remoteShows.withIndex().mapNotNull { (index, show) ->
                val tmdbId = show.tmdbId ?: return@mapNotNull null
                Triple(index, show, tmdbId)
            }

            val cachedPosterIds = tvShowsDao.getTmdbIdsWithPoster(withTmdbId.map { it.third })

            withTmdbId.map { (index, show, tmdbId) ->
                async {
                    val tmdbDetails = if (tmdbId in cachedPosterIds) {
                        null
                    } else {
                        runCatching { tmdbDetailsDataSource.getShowDetails(tmdbId) }
                            .getOrNull()
                            ?.let { (it as? ApiResponse.Success)?.body }
                    }

                    SearchFetchResult(
                        remoteShow = show,
                        tmdbId = tmdbId,
                        tmdbDetails = tmdbDetails,
                        position = index,
                        provider = activeSource.provider,
                    )
                }
            }.awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of<String, List<SearchFetchResult>, List<ShowEntity>>(
        reader = { key -> searchDao.observeResults(key) },
        writer = { key, results ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    searchDao.deleteResults(key)

                    results.forEach { result ->
                        tvShowsDao.upsertMerging(result.toShowToPersist(formatterUtil, dateTimeProvider))
                        tvShowsDao.upsertExternalId(
                            tmdbId = result.tmdbId,
                            provider = result.provider.toDbProvider(),
                            externalId = result.remoteShow.providerShowId,
                        )
                        searchDao.upsertResult(
                            query = key,
                            tmdbId = result.tmdbId,
                            position = result.position.toLong(),
                            score = result.remoteShow.score,
                        )
                    }

                    requestManagerRepository.upsert(
                        entityId = key.hashCode().toLong(),
                        requestType = SEARCH_RESULTS.name,
                    )
                }
            }
        },
        delete = { key -> searchDao.deleteResults(key) },
        deleteAll = { searchDao.deleteAllResults() },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).build()

private const val SEARCH_LIMIT = 30

internal data class SearchFetchResult(
    val remoteShow: RemoteSearchShow,
    val tmdbId: Long,
    val tmdbDetails: TmdbShowDetailsResponse?,
    val position: Int,
    val provider: SyncProviderSource,
)

private fun SearchFetchResult.toShowToPersist(
    formatterUtil: FormatterUtil,
    dateTimeProvider: DateTimeProvider,
): ShowToPersist {
    val remote = remoteShow
    val tmdb = tmdbDetails
    return ShowToPersist(
        showId = if (provider == SyncProviderSource.TRAKT) Id(remote.providerShowId.toLong()) else null,
        tmdbId = Id(tmdbId),
        name = remote.title,
        overview = remote.overview ?: tmdb?.overview ?: "",
        language = remote.language ?: tmdb?.originalLanguage,
        year = remote.year?.toString() ?: tmdb?.firstAirDate?.let { dateTimeProvider.extractYear(it) },
        status = remote.status ?: tmdb?.status,
        ratings = tmdb?.voteAverage ?: remote.rating ?: 0.0,
        voteCount = tmdb?.voteCount?.toLong() ?: remote.votes ?: 0L,
        genres = remote.genres?.map(::traktGenreName) ?: tmdb?.genres?.map { it.name },
        posterPath = tmdb?.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdropPath = tmdb?.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        episodeNumbers = (remote.episodeCount ?: tmdb?.numberOfEpisodes)?.toString(),
        seasonNumbers = null,
    )
}
