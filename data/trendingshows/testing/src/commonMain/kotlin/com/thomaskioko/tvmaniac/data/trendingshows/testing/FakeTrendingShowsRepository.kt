package com.thomaskioko.tvmaniac.data.trendingshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeTrendingShowsRepository : TrendingShowsRepository {

    private var showEntityList: Channel<List<ShowEntity>> = Channel(Channel.UNLIMITED)
    private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
        Channel(Channel.UNLIMITED)
    private var pagedList: Channel<PagingData<ShowEntity>> =
        Channel(Channel.UNLIMITED)

    suspend fun setTrendingShows(result: List<ShowEntity>) {
        showEntityList.send(result)
    }

    suspend fun setObserveTrendingShows(result: Either<Failure, List<ShowEntity>>) {
        entityListResult.send(result)
    }

    suspend fun setPagedData(result: PagingData<ShowEntity>) {
        pagedList.send(result)
    }

    override suspend fun fetchTrendingShows(
        forceRefresh: Boolean,
    ): List<ShowEntity> = showEntityList.receive()

    override fun observeTrendingShows(): Flow<Either<Failure, List<ShowEntity>>> {
        return entityListResult.receiveAsFlow()
    }

    override fun getPagedTrendingShows(): Flow<PagingData<ShowEntity>> {
        return pagedList.receiveAsFlow()
    }
}
