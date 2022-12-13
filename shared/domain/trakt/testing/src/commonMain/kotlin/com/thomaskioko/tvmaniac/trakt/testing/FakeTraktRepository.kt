package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


class FakeTraktRepository : TraktRepository {

    private var categoryResult: Flow<Resource<List<SelectShowsByCategory>>> =
        flowOf(Resource.Success(data = null))

    private var showResult: Flow<Resource<SelectByShowId>> =
        flowOf(Resource.Success(data = null))

    private var followedResult: Flow<Resource<List<SelectFollowedShows>>> =
        flowOf(Resource.Success(data = null))

    suspend fun setCategoryResult(result: Resource<List<SelectShowsByCategory>>) {
        categoryResult = flow { emit(result) }
    }

    suspend fun setShowResult(result: Resource<SelectByShowId>) {
        showResult = flow { emit(result) }
    }

    suspend fun setFollowedResult(result: Resource<List<SelectFollowedShows>>) {
        followedResult = flow { emit(result) }
    }

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> = flowOf(
        Resource.Success(
            Trakt_user(
                slug = "me",
                user_name = "silly_eyes",
                full_name = "Stranger Danger",
                profile_picture = "",
                is_me = true
            )
        )
    )

    override fun observeStats(slug: String, refresh: Boolean): Flow<Resource<TraktStats>> =
        flowOf(
            Resource.Success(
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

    override fun observeCreateTraktList(userSlug: String): Flow<Resource<Trakt_list>> =
        flowOf(
            Resource.Success(
                Trakt_list(
                    id = 45,
                    slug = "favorites",
                    description = "Favorite Shows"
                )
            )
        )

    override fun observeFollowedShows(): Flow<Resource<List<SelectFollowedShows>>> = followedResult

    override fun getFollowedShows(): List<SelectFollowedShows> = cachedShowResult

    override fun observeUpdateFollowedShow(
        traktId: Int,
        addToWatchList: Boolean
    ): Flow<Resource<Unit>> = flowOf(Resource.Success(Unit))

    override fun observeShow(traktId: Int): Flow<Resource<SelectByShowId>> = showResult

    override fun fetchShowsByCategoryId(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>> =
        categoryResult

    override fun observeCachedShows(categoryId: Int): Flow<Resource<List<SelectShowsByCategory>>> =
        flow {
            categoryResult.collect {
                emit(it)
            }
        }

    override suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean) {}

    override suspend fun fetchTraktWatchlistShows() {}

    override suspend fun fetchShows() {}

    override suspend fun syncFollowedShows() {}

}
