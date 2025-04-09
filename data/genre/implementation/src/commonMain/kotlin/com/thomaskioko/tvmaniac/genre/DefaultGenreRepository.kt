package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.core.store.mapToEither
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreRepository(
  private val store: GenreStore,
  private val showsByGenreIdStore: ShowsByGenreIdStore,
  private val genreDao: GenreDao,
  private val dispatchers: AppCoroutineDispatchers,
) : GenreRepository {

  override fun observeGenresWithShows(forceRefresh: Boolean): Flow<Either<Failure, List<ShowGenresEntity>>> {
    return store
      .stream(
        StoreReadRequest.cached(
          key = Unit,
          refresh = forceRefresh || genreDao.getGenres().isEmpty(),
        ),
      )
      .mapToEither()
      .flowOn(dispatchers.io)
  }

  override suspend fun observeGenreByShowId(id: String, forceRefresh: Boolean): Flow<Either<Failure, List<Tvshow>>> {
    return showsByGenreIdStore
      .stream(
        StoreReadRequest.cached(
          key = id,
          refresh = forceRefresh || shouldRefresh(id),
        ),
      )
      .mapToEither()
      .flowOn(dispatchers.io)
  }

  //Update implementation using RequestManager to check if we ne
  private suspend fun shouldRefresh(id: String): Boolean {
    val list  = genreDao.observeShowsByGenreId(id).first()
    return list.isEmpty() || list.size < 20
  }
}
