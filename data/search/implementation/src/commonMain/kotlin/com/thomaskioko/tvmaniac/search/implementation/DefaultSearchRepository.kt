package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultSearchRepository(
  private val tvShowsDao: TvShowsDao,
  private val store: SearchShowStore,
  private val dispatchers: AppCoroutineDispatchers,
) : SearchRepository {
  override suspend fun search(query: String): Flow<Either<Failure, List<ShowEntity>>> =
    store.stream(
      StoreReadRequest.cached(
        key = query,
        refresh = shouldRefresh(query),
      ),
    )
      .mapResult(store.get(key = query))
      .flowOn(dispatchers.io)

  private suspend fun shouldRefresh(query: String): Boolean {
    return when {
      query.length < 2 -> false
      hasNoLocalData(query) -> true
      else -> false
    }
  }

  private suspend fun hasNoLocalData(query: String): Boolean =
    tvShowsDao.observeShowsByQuery(query)
      .first()
      .isEmpty()

}
