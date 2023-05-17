package com.thomaskioko.tvmaniac.profile.implementation.stats

import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.profile.api.stats.StatsDao
import com.thomaskioko.tvmaniac.profile.api.stats.StatsRepository
import com.thomaskioko.tvmaniac.profile.implementation.ProfileResponseMapper
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class StatsRepositoryImpl(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val statsDao: StatsDao,
    private val mapper: ProfileResponseMapper,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : StatsRepository {

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, User_stats>> =
        networkBoundResult(
            query = { statsDao.observeStats() },
            shouldFetch = { it == null || refresh },
            fetch = { traktRemoteDataSource.getUserStats(slug) },
            saveFetchResult = { statsDao.insert(mapper.toTraktStats(slug, it)) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )
}
