package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
@SingleIn(AppScope::class)
class GenreStore(
    private val genreDao: GenreDao,
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val scope: AppCoroutineScope,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<ShowGenresEntity>> by storeBuilder(
    fetcher = Fetcher.of { _: Unit ->
        when (val response = tmdbRemoteDataSource.getShowGenres()) {
            is ApiResponse.Success -> {
                val genres = response.body.genres
                    .filter { genre -> genre.name != "News" || genre.name != "Talk" }

                val results = mutableListOf<ShowGenresEntity>()

                genres.forEach { genre ->
                    val entity = ShowGenresEntity(
                        id = genre.id.toLong(),
                        name = genre.name,
                        posterUrl = null, // Poster URL will be updated by GenrePosterStore
                    )
                    results.add(entity)

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
                results
            }
            is ApiResponse.Error.GenericError -> throw Throwable(response.errorMessage)
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable(response.errorMessage)
        }
    },
    sourceOfTruth = SourceOfTruth.of<Unit, List<ShowGenresEntity>, List<ShowGenresEntity>>(
        reader = { _: Unit -> genreDao.observeGenres() },
        writer = { _: Unit, _ ->
            //  Writing is now handled in the fetcher
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
)
    .scope(scope.io)
    .build()
