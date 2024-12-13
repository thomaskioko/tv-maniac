package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import me.tatarka.inject.annotations.Inject
import com.thomaskioko.tvmaniac.db.Id
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class GenreStore(
  private val genreDao: GenreDao,
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val scope: AppCoroutineScope,
) : Store<Unit, List<ShowGenresEntity>> by StoreBuilder.from(
  fetcher = Fetcher.of { _: Unit ->
    when (val response = tmdbRemoteDataSource.getGenre()) {
      is ApiResponse.Success -> response.body
      is ApiResponse.Error.GenericError -> {
        throw Throwable("${response.errorMessage}")
      }
      is ApiResponse.Error.HttpError -> {
        throw Throwable("${response.code} - ${response.errorMessage}")
      }
      is ApiResponse.Error.SerializationError -> {
        throw Throwable("${response.errorMessage}")
      }
    }
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { _: Unit -> genreDao.observeGenresWithShows() },
    writer = { _: Unit, response ->
      response.genres.map { genre ->
        genreDao.upsert(
          Genres(
            id = Id(genre.id.toLong()),
            name = genre.name,
          ),
        )
      }
    },
  ),
)
  .scope(scope.io)
  .build()
