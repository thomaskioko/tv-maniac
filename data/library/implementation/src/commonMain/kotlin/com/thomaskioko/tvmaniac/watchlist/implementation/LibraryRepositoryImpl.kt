package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.shows.api.LibraryDao
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.util.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.DefaultError
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class LibraryRepositoryImpl(
    private val remoteDataSource: TraktListRemoteDataSource,
    private val libraryDao: LibraryDao,
    private val profileDao: ProfileDao,
    private val dateFormatter: PlatformDateFormatter,
    private val exceptionHandler: NetworkExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : LibraryRepository {

    override suspend fun syncLibrary() {
        profileDao.observeUser()
            .flowOn(dispatchers.io)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    libraryDao.getUnSyncedShows()
                        .map {
                            remoteDataSource.addShowToWatchList(it.id.id)

                            libraryDao.upsert(
                                Watchlist(
                                    id = it.id,
                                    synced = true,
                                    created_at = dateFormatter.getTimestampMilliseconds(),
                                ),
                            )
                        }
                }
            }
    }

    override suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean) {
        // TODO:: Check if user is signed into trakt and sync followed shows.
        when {
            addToLibrary -> libraryDao.upsert(
                Watchlist(
                    id = Id(traktId),
                    synced = false,
                    created_at = dateFormatter.getTimestampMilliseconds(),
                ),
            )

            else -> libraryDao.removeShow(traktId)
        }
    }

    override fun observeLibrary(): Flow<Either<Failure, List<WatchedShow>>> =
        libraryDao.observeWatchedShows()
            .distinctUntilChanged()
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override suspend fun getLibraryShows(): List<WatchedShow> = libraryDao.getWatchedShows()
}
