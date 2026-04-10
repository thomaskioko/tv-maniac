package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
@SingleIn(AppScope::class)
public class GenreStore(
    private val genreDao: GenreDao,
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    @IoCoroutineScope private val scope: CoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<ShowGenresEntity>> by storeBuilder(
    fetcher = apiFetcher { tmdbRemoteDataSource.getShowGenres() },
    sourceOfTruth = SourceOfTruth.of<Unit, TmdbGenreResult, List<ShowGenresEntity>>(
        reader = { _ -> genreDao.observeGenres() },
        writer = { _, response ->

            response.genres
                .filter { genre -> genre.name != "News" || genre.name != "Talk" }
                .forEach { genre ->
                    val entity = ShowGenresEntity(
                        id = genre.id.toLong(),
                        name = genre.name,
                        posterUrl = null, // Poster URL will be updated by GenrePosterStore
                    )

                    withContext(dispatchers.databaseWrite) {
                        genreDao.upsert(
                            Genres(
                                id = Id(entity.id),
                                name = entity.name,
                                poster_url = null, // Poster URL will be updated by GenrePosterStore
                            ),
                        )
                    }
                }
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
)
    .scope(scope)
    .build()
