package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
@SingleIn(AppScope::class)
public class ShowsByGenreIdStore(
    private val genreDao: GenreDao,
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val formatterUtil: FormatterUtil,
    @IoCoroutineScope private val scope: CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, List<Tvshow>> by storeBuilder(
    fetcher = apiFetcher { id: String ->
        tmdbRemoteDataSource.discoverShows(genres = id)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { id: String -> genreDao.observeShowsByGenreId(id) },
        writer = { id: String, response: TmdbShowResult ->
            response.results.forEach { genre ->
                genreDao.upsert(
                    Genres(
                        id = Id(id.toLong()),
                        name = genre.name,
                        poster_url = genre.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    ),
                )
            }
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
)
    .scope(scope)
    .build()
