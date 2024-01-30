package com.thomaskioko.tvmaniac.data.popularshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow

class FakePopularShowsRepository : PopularShowsRepository {

    private var showEntityList: Channel<List<ShowEntity>> = Channel(Channel.UNLIMITED)
    private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
        Channel(Channel.UNLIMITED)
    private var pagedList: Channel<PagingData<ShowEntity>> =
        Channel(Channel.UNLIMITED)

    suspend fun setPopularShows(result: List<ShowEntity>) {
        showEntityList.send(result)
    }

    suspend fun setObservePopularShows(result: Either<Failure, List<ShowEntity>>) {
        entityListResult.send(result)
    }

    suspend fun setPagedData(result: PagingData<ShowEntity>) {
        pagedList.send(result)
    }
    override suspend fun fetchPopularShows(forceRefresh: Boolean): List<ShowEntity> {
        return showEntityList.receive()
    }

    override fun observePopularShows(): Flow<Either<Failure, List<ShowEntity>>> {
        return entityListResult.receiveAsFlow()
    }

    override fun getPagedPopularShows(): Flow<PagingData<ShowEntity>> {
        return flowOf(PagingData.empty())
    }
}