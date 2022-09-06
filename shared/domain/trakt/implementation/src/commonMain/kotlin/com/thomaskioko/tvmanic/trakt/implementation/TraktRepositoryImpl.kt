package com.thomaskioko.tvmanic.trakt.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFavoriteListCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmanic.trakt.implementation.mapper.toShow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TraktRepositoryImpl constructor(
    private val tvShowCache: TvShowCache,
    private val traktUserCache: TraktUserCache,
    private val followedCache: TraktFollowedCache,
    private val favoriteCache: TraktFavoriteListCache,
    private val traktService: TraktService,
    private val tmdbRepository: TmdbRepository,
    private val dispatcher: CoroutineDispatcher,
) : TraktRepository {

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> =
        networkBoundResource(
            query = { traktUserCache.observeMe() },
            shouldFetch = { it == null },
            fetch = { traktService.getUserProfile(slug) },
            saveFetchResult = { it.mapAndCache(slug) },
            onFetchFailed = { Logger.withTag("observeMe").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override fun observeCreateTraktFavoriteList(userSlug: String): Flow<Resource<Trakt_favorite_list>> =
        networkBoundResource(
            query = { favoriteCache.observeFavoriteList() },
            shouldFetch = { it == null },
            fetch = { traktService.createFavoriteList(userSlug) },
            saveFetchResult = { it.mapAndCache() },
            onFetchFailed = { Logger.withTag("createTraktFavoriteList").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override fun observeFollowedShows(listId: Int, userSlug: String): Flow<Resource<Unit>> =
        networkBoundResource(
            query = { flowOf(Unit) },
            shouldFetch = { true },
            fetch = { traktService.getFollowedList(listId, userSlug) },
            saveFetchResult = { it.mapAndCache() },
            onFetchFailed = { Logger.withTag("observeFollowedShows").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override suspend fun syncFollowedShows() {
        traktUserCache.observeMe()
            .flowOn(dispatcher)
            .collect { user ->
                user?.let {
                    traktService.getUserList(user.slug)
                        .firstOrNull()?.let { listResponse ->
                            listResponse.mapAndCache()

                            traktService.getFollowedList(listResponse.ids.trakt, user.slug)
                                .mapAndCache()
                        }

                    followedCache.getUnsyncedFollowedShows()
                        .map {
                            tmdbRepository.observeShow(it.id)
                                .collect { showResource ->
                                    showResource.data?.let { show ->
                                        followedCache.updateShowSyncState(show.trakt_id)
                                    }
                                }
                        }
                }
            }
    }

    override fun observeUpdateFollowedShow(
        traktId: Int,
        addToWatchList: Boolean
    ): Flow<Resource<Unit>> = networkBoundResource(
        query = { flowOf(Unit) },
        shouldFetch = { traktUserCache.getMe() != null },
        fetch = {
            val user = traktUserCache.getMe()
            val listId: Int = getOrCreateTraktList(user!!)

            if (addToWatchList) {
                traktService.addShowToList(user.slug, listId, traktId).added.shows
            } else {
                traktService.deleteShowFromList(user.slug, listId, traktId).deleted.shows
            }
        },
        saveFetchResult = {
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        id = traktId,
                        synced = true
                    )
                )
                else -> followedCache.removeShow(traktId)
            }
        },
        onFetchFailed = {
            //If something wrong happens on the network layer, we still want to cache it and sync later.
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        id = traktId,
                        synced = false
                    )
                )
                else -> followedCache.removeShow(traktId)
            }
            Logger.withTag("observeUpdateFollowedShow").e(it.resolveError())
        },
        coroutineDispatcher = dispatcher
    )

    override fun observeShow(traktId: Int): Flow<Resource<Show>> = networkBoundResource(
        query = { tvShowCache.observeTvShow(traktId) },
        shouldFetch = { it == null },
        fetch = { traktService.getSeasonDetails(traktId) },
        saveFetchResult = { response -> tvShowCache.insert(response.toShow()) },
        onFetchFailed = { Logger.withTag("observeShow").e { it.resolveError() } },
        coroutineDispatcher = dispatcher
    )

    override suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean) {
        when {
            addToWatchList -> followedCache.insert(
                Followed_shows(
                    id = traktId,
                    synced = false
                )
            )
            else -> followedCache.removeShow(traktId)
        }
    }

    private suspend fun getOrCreateTraktList(
        user: Trakt_user
    ): Int {
        val traktFollowedList = favoriteCache.getFavoriteList()

        return if (traktFollowedList == null) {
            val listResponse = traktService.createFavoriteList(user.slug)
            listResponse.ids.trakt
        } else {
            traktFollowedList.id
        }
    }

    override fun observeFollowedShows(): Flow<List<SelectFollowedShows>> =
        followedCache.observeFollowedShows()

    override fun observeFollowedShow(traktId: Int): Flow<Boolean> =
        followedCache.observeFollowedShow(traktId)
            .map { it?.id == traktId }

    private fun TraktUserResponse.mapAndCache(slug: String) {
        traktUserCache.insert(
            Trakt_user(
                slug = ids.slug,
                full_name = name,
                user_name = userName,
                profile_picture = images.avatar.full,
                is_me = slug == "me"
            )
        )
    }

    private fun TraktCreateListResponse.mapAndCache() {
        favoriteCache.insert(
            Trakt_favorite_list(
                id = ids.trakt,
                slug = ids.slug,
                description = description
            )
        )
    }

    private fun TraktPersonalListsResponse.mapAndCache() {
        favoriteCache.insert(
            Trakt_favorite_list(
                id = ids.trakt,
                slug = ids.slug,
                description = description
            )
        )
    }

    private fun List<TraktFollowedShowResponse>.mapAndCache() {
        map {
            followedCache.insert(
                Followed_shows(
                    id = it.show.ids.trakt,
                    synced = false
                )
            )
        }
    }
}