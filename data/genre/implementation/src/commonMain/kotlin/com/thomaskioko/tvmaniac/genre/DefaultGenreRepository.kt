package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreRepository(
  private val store: GenreStore,
  private val genreDao: GenreDao,
  private val dispatchers: AppCoroutineDispatchers,
) : GenreRepository {

  override suspend fun observeGenresWithShows(forceRefresh: Boolean): Flow<Either<Failure, List<ShowGenresEntity>>> {
    return store
      .stream(
        StoreReadRequest.cached(
          key = Unit,
          refresh = forceRefresh || genreDao.getGenres().isEmpty(),
        ),
      )
      .mapResult(store.get(key = Unit))
      .flowOn(dispatchers.io)
  }
}
