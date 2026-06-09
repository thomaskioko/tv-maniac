package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
public class SearchShowStore(
    private val tvShowsDao: TvShowsDao,
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { query: String ->
        coroutineScope {
            traktRemoteDataSource.searchShows(query).getOrThrow()
                .mapNotNull { searchResult ->
                    if (searchResult.type != "show") return@mapNotNull null
                    val show = searchResult.show ?: return@mapNotNull null
                    val tmdbId = show.ids.tmdb ?: return@mapNotNull null

                    async {
                        val tmdbResult = runCatching {
                            tmdbDataSource.getShowDetails(tmdbId)
                        }
                        SearchShowResult(
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
    sourceOfTruth = SourceOfTruth.of<String, List<SearchShowResult>, List<ShowEntity>>(
        reader = { query: String -> tvShowsDao.observeShowsByQuery(query) },
        writer = { _, shows ->
            val tvShows = shows.map { result ->
                result.toTvshow(formatterUtil, dateTimeProvider)
            }
            tvShowsDao.upsert(tvShows)
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).build()

private fun SearchShowResult.toTvshow(formatterUtil: FormatterUtil, dateTimeProvider: DateTimeProvider): ShowToPersist {
    val tmdb = tmdbDetails
    return ShowToPersist(
        showId = Id(traktShow.ids.trakt),
        tmdbId = Id(tmdbId),
        name = traktShow.title,
        overview = traktShow.overview ?: "",
        language = traktShow.language,
        status = traktShow.status,
        year = traktShow.firstAirDate?.let { dateTimeProvider.extractYear(it) },
        episodeNumbers = traktShow.airedEpisodes?.toString(),
        ratings = tmdb?.voteAverage ?: 0.0,
        voteCount = traktShow.votes ?: 0L,
        posterPath = tmdb?.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdropPath = tmdb?.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        genres = traktShow.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
        seasonNumbers = null,
    )
}
