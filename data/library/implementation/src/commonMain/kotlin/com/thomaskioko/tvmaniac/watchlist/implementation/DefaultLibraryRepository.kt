package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.db.Library
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.model.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.shows.api.LibraryDao
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
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
class DefaultLibraryRepository(
  private val libraryDao: LibraryDao,
  private val dateFormatter: PlatformDateFormatter,
  private val exceptionHandler: NetworkExceptionHandler,
) : LibraryRepository {

  override suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean) {
    when {
      addToLibrary ->
        libraryDao.upsert(
          Library(
            id = Id(traktId),
            created_at = dateFormatter.getTimestampMilliseconds(),
          ),
        )
      else -> libraryDao.delete(traktId)
    }
  }

  override fun observeLibrary(): Flow<Either<Failure, List<LibraryShows>>> =
    libraryDao
      .observeShowsInLibrary()
      .distinctUntilChanged()
      .map { Either.Right(it) }
      .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
}
