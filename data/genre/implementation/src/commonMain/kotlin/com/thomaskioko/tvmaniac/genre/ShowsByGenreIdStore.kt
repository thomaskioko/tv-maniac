package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class ShowsByGenreIdStore(
  private val genreDao: GenreDao,
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val formatterUtil: FormatterUtil,
  private val scope: AppCoroutineScope,
) : Store<String, List<Tvshows>> by StoreBuilder.from(
  fetcher = Fetcher.of { id: String ->
    when (val response = tmdbRemoteDataSource.discoverShows(genres = id)) {
        is ApiResponse.Success -> response.body
        is ApiResponse.Error.GenericError -> throw Throwable(response.errorMessage)
        is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
        is ApiResponse.Error.SerializationError -> throw Throwable(response.errorMessage)
      }
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { id: String -> genreDao.observeShowsByGenreId(id) },
    writer = { id: String, response ->
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
  ),
)
  .scope(scope.io)
  .build()

