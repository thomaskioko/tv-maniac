package com.thomaskioko.tvmaniac.profile.implementation.followed

import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.profile.api.followed.FollowedRepository
import com.thomaskioko.tvmaniac.shows.api.cache.FollowedCache
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@Inject
class FollowedRepositoryImpl(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val profileDao: ProfileDao,
    private val followedCache: FollowedCache,
    private val dateFormatter: DateFormatter,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : FollowedRepository {

    override fun observeUpdateFollowedShow(
        traktId: Long,
        addToWatchList: Boolean,
    ): Flow<Either<Failure, Unit>> = networkBoundResult(
        query = { flowOf(Unit) },
        shouldFetch = { profileDao.getUser() != null },
        fetch = {
            val user = profileDao.getUser()

            if (user != null) {
                if (addToWatchList) {
                    traktRemoteDataSource.addShowToWatchList(traktId).added.shows
                } else {
                    traktRemoteDataSource.removeShowFromWatchList(traktId).deleted.shows
                }
            }
        },
        saveFetchResult = {
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        id = traktId,
                        synced = true,
                        created_at = dateFormatter.getTimestampMilliseconds(),
                    ),
                )

                else -> followedCache.removeShow(traktId)
            }
        },
        exceptionHandler = exceptionHandler,
        coroutineDispatcher = dispatchers.io,
    )

    override suspend fun syncFollowedShows() {
        profileDao.observeUser()
            .flowOn(dispatchers.io)
            .collect { user ->
                if (user.slug.isNotBlank()) {
                    followedCache.getUnsyncedFollowedShows()
                        .map {
                            traktRemoteDataSource.addShowToWatchList(it.id)

                            followedCache.insert(
                                Followed_shows(
                                    id = it.id,
                                    synced = true,
                                    created_at = dateFormatter.getTimestampMilliseconds(),
                                ),
                            )
                        }
                }
            }
    }
}