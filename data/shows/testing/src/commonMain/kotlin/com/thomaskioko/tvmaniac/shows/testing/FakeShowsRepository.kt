package com.thomaskioko.tvmaniac.shows.testing

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeShowsRepository : ShowsRepository {

    private var featuredResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var anticipatedResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var popularResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var trendingResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var showResult = flowOf<Either<Failure, SelectByShowId>>()

    suspend fun setFeaturedResult(result: Either<Failure, List<SelectShowsByCategory>>) {
        featuredResult = flow { emit(result) }
    }

    suspend fun setAnticipatedResult(result: Either<Failure, List<SelectShowsByCategory>>) {
        anticipatedResult = flow { emit(result) }
    }

    suspend fun setPopularResult(result: Either<Failure, List<SelectShowsByCategory>>) {
        popularResult = flow { emit(result) }
    }

    suspend fun setTrendingResult(result: Either<Failure, List<SelectShowsByCategory>>) {
        trendingResult = flow { emit(result) }
    }

    suspend fun setShowResult(result: Either<Failure, SelectByShowId>) {
        showResult = flow { emit(result) }
    }

    override fun observeShow(traktId: Long): Flow<Either<Failure, SelectByShowId>> = showResult

    override fun observeCachedShows(
        categoryId: Long,
    ): Flow<Either<Failure, List<SelectShowsByCategory>>> = featuredResult

    override fun fetchTrendingShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        trendingResult

    override fun observeTrendingCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        trendingResult

    override fun fetchPopularShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        popularResult

    override fun observePopularCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        popularResult

    override fun fetchAnticipatedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        anticipatedResult

    override fun observeAnticipatedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        anticipatedResult

    override fun fetchFeaturedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        featuredResult

    override fun observeFeaturedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        featuredResult

    override suspend fun fetchShows() {}
}
