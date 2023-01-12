package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


class FakeTraktRepository : TraktRepository {

    private var featuredResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var anticipatedResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var popularResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var trendingResult = flowOf<Either<Failure, List<SelectShowsByCategory>>>()

    private var showResult = flowOf<Either<Failure, SelectByShowId>>()

    private var followedResult = flowOf<Either<Failure, List<SelectFollowedShows>>>()

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

    suspend fun setFollowedResult(result: Either<Failure, List<SelectFollowedShows>>) {
        followedResult = flow { emit(result) }
    }

    override fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>> = flowOf(
        Either.Right(
            Trakt_user(
                slug = "me",
                user_name = "silly_eyes",
                full_name = "Stranger Danger",
                profile_picture = "",
                is_me = true
            )
        )
    )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Either<Failure, TraktStats>> =
        flowOf(
            Either.Right(
                TraktStats(
                    user_slug = "me",
                    months = "148",
                    days = "54",
                    hours = "142",
                    collected_shows = "1200",
                    episodes_watched = "8200"
                )
            )
        )

    override fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>> =
        flowOf(
            Either.Right(
                Trakt_list(
                    id = 45,
                    slug = "favorites",
                    description = "Favorite Shows"
                )
            )
        )

    override fun observeFollowedShows(): Flow<Either<Failure, List<SelectFollowedShows>>> =
        followedResult

    override fun getFollowedShows(): List<SelectFollowedShows> = cachedShowResult

    override fun observeUpdateFollowedShow(
        traktId: Int,
        addToWatchList: Boolean
    ): Flow<Either<Failure, Unit>> = flowOf(Either.Right(Unit))

    override fun observeShow(traktId: Int): Flow<Either<Failure, SelectByShowId>> = showResult

    override fun observeCachedShows(
        categoryId: Int
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

    override suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean) {}

    override suspend fun fetchTraktWatchlistShows() {}

    override suspend fun fetchShows() {}

    override suspend fun syncFollowedShows() {}

}
