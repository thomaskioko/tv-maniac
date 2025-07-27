package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
@SingleIn(AppScope::class)
class GenrePosterStore(
    private val genreDao: GenreDao,
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val formatterUtil: FormatterUtil,
    private val scope: AppCoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowGenresEntity>> by storeBuilder(
    fetcher = apiFetcher { id ->
        tmdbRemoteDataSource.discoverShows(
            genres = id.toString(),
            watchProviders = "8,15,283,318,337,350,1899",
        )
    },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowResult, List<ShowGenresEntity>>(
        reader = { genreId: Long -> genreDao.observeGenres() },
        writer = { genreId, response ->
            val posterPath = response.results.shuffled().firstOrNull()?.posterPath
            val posterUrl = posterPath?.let { formatterUtil.formatTmdbPosterPath(it) }
            val genre = genreDao.getGenre(genreId)

            withContext(dispatchers.databaseWrite) {
                genreDao.upsert(
                    Genres(
                        id = Id(genreId),
                        name = genre.name,
                        poster_url = posterUrl,
                    ),
                )
            }
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
)
    .scope(scope.io)
    .build()
