package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsRepository {
    suspend fun fetchUpcomingShows(): List<ShowEntity>
    fun observeUpcomingShows(): Flow<Either<Failure, List<ShowEntity>>>
    fun getPagedUpcomingShows(): Flow<PagingData<ShowEntity>>
}
