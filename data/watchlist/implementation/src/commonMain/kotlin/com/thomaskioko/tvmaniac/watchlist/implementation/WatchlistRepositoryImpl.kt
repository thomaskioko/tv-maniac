package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.core.db.Watchlist
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.NetworkExceptionHandler
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
class WatchlistRepositoryImpl(
    private val remoteDataSource: TraktListRemoteDataSource,
    private val watchlistDao: WatchlistDao,
    private val profileDao: ProfileDao,
    private val dateFormatter: DateFormatter,
    private val exceptionHandler: NetworkExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistRepository {

    override suspend fun syncWatchlist() {
        profileDao.observeUser()
            .flowOn(dispatchers.io)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    watchlistDao.getUnSyncedShows()
                        .map {
                            remoteDataSource.addShowToWatchList(it.id.id)

                            watchlistDao.upsert(
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

    override suspend fun updateWatchlist(traktId: Long, addToWatchList: Boolean) {
        // TODO:: Check if user is signed into trakt and sync followed shows.
        when {
            addToWatchList -> watchlistDao.upsert(
                Watchlist(
                    id = Id(traktId),
                    synced = false,
                    created_at = dateFormatter.getTimestampMilliseconds(),
                ),
            )

            else -> watchlistDao.removeShow(traktId)
        }
    }

    override fun observeWatchList(): Flow<Either<Failure, List<WatchedShow>>> =
        watchlistDao.observeWatchedShows()
            .distinctUntilChanged()
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override suspend fun getWatchlist(): List<WatchedShow> = watchlistDao.getWatchedShows()
}
