package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.db.Genres
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
class GenreStore(
  private val genreDao: GenreDao,
  private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
  private val formatterUtil: FormatterUtil,
  private val scope: AppCoroutineScope,
) : Store<Unit, List<ShowGenresEntity>> by StoreBuilder.from(
  fetcher = Fetcher.of { _: Unit ->
    when (val response = tmdbRemoteDataSource.getGenre()) {
      is ApiResponse.Success -> {
        val genres = response.body.genres
          .filter { genre -> genre.name != "News" || genre.name != "Talk" }

        // Process genres sequentially but emit results progressively
        val results = mutableListOf<ShowGenresEntity>()

        genres.forEach { genre ->
          val discoverResponse = tmdbRemoteDataSource.discoverShows(
            genres = genre.id.toString(),
            watchProviders = "8,15,283,318,337,350,1899",
          )

          when (discoverResponse) {
            is ApiResponse.Success -> {
              val entity = ShowGenresEntity(
                id = genre.id.toLong(),
                name = genre.name,
                posterUrl = discoverResponse.body.results.shuffled().firstOrNull()?.posterPath,
              )
              results.add(entity)

              // Update the database immediately for each genre
              genreDao.upsert(
                Genres(
                  id = Id(entity.id),
                  name = entity.name,
                  poster_url = entity.posterUrl?.let { formatterUtil.formatTmdbPosterPath(it) },
                )
              )
            }
            else -> { /* Skip failed genres */ }
          }
        }
        results
      }
      is ApiResponse.Error.GenericError -> throw Throwable(response.errorMessage)
      is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
      is ApiResponse.Error.SerializationError -> throw Throwable(response.errorMessage)
    }
  },
  sourceOfTruth = SourceOfTruth.of(
    reader = { _: Unit -> genreDao.observeGenres() },
    writer = { _: Unit, reponse -> /* Writing is now handled in the fetcher */ },
  ),
)
  .scope(scope.io)
  .build()
