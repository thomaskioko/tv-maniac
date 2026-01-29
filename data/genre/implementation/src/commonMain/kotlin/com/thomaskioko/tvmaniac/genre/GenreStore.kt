package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class GenreStore(
    private val genreDao: GenreDao,
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val scope: AppCoroutineScope,
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
    .scope(scope.io)
    .build()
