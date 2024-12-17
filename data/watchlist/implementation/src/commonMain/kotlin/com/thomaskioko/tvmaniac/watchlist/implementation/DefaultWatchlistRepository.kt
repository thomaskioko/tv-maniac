package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.core.db.Watchlists
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.model.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistRepository(
  private val watchlistDao: WatchlistDao,
  private val dateFormatter: PlatformDateFormatter,
  private val exceptionHandler: NetworkExceptionHandler,
) : WatchlistRepository {

  override suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean) {
    when {
      addToLibrary ->
        watchlistDao.upsert(
          Watchlist(
            id = Id(traktId),
            created_at = dateFormatter.getTimestampMilliseconds(),
          ),
        )
      else -> watchlistDao.delete(traktId)
    }
  }

  override fun observeWatchlist(): Flow<Either<Failure, List<Watchlists>>> =
    watchlistDao
      .observeShowsInWatchlist()
      .distinctUntilChanged()
      .map { Either.Right(it) }
      .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

  override fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchWatchlist>>> {
    return watchlistDao
      .observeWatchlistByQuery(query)
      .map { Either.Right(it) }
      .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
  }
}
