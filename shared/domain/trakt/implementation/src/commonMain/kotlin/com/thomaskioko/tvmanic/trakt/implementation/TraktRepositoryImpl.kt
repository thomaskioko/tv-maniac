package com.thomaskioko.tvmanic.trakt.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.Trakt_favorite_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TraktRepositoryImpl constructor(
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

    override fun observeAddShowToTraktFavoriteList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): Flow<Resource<Unit>> = networkBoundResource(
        query = { flowOf(Unit) },
        shouldFetch = { true },
        fetch = { traktService.addShowToList(userSlug, listId, tmdbShowId) },
        saveFetchResult = {
            followedCache.insert(
                Followed_shows(
                    show_id = it.added.shows.toLong(),
                    synced = true
                )
            )
        },
        onFetchFailed = { Logger.withTag("addShowToTraktFavoriteList").e(it.resolveError()) },
        coroutineDispatcher = dispatcher
    )

    override fun observeRemoveShowFromTraktFavoriteList(
        userSlug: String,
        listId: Long,
        tmdbShowId: Long
    ): Flow<Resource<Unit>> = networkBoundResource(
        query = { flowOf(Unit) },
        shouldFetch = { true },
        fetch = { traktService.deleteShowFromList(userSlug, listId, tmdbShowId) },
        saveFetchResult = { followedCache.removeShow(it.deleted.shows.toLong()) },
        onFetchFailed = { Logger.withTag("removeShowFromTraktFavoriteList").e(it.resolveError()) },
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
                            tmdbRepository.observeShow(it.show_id)
                                .collect { showResource ->
                                    showResource.data?.let { show ->
                                        followedCache.updateShowSyncState(show.id)
                                    }
                                }
                        }
                }
            }
    }

    override fun observeUpdateFollowedShow(
        showId: Long,
        addToWatchList: Boolean
    ): Flow<Resource<Unit>> = networkBoundResource(
        query = { flowOf(Unit) },
        shouldFetch = { traktUserCache.getMe() != null },
        fetch = {
            val user = traktUserCache.getMe()
            val listId: Long = getOrCreateTraktList(user!!)

            if (addToWatchList) {
                traktService.addShowToList(user.slug, listId, showId).added.shows
            } else {
                traktService.deleteShowFromList(user.slug, listId, showId).deleted.shows
            }
        },
        saveFetchResult = {
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        show_id = showId,
                        synced = true
                    )
                )
                else -> followedCache.removeShow(showId)
            }
        },
        onFetchFailed = {
            //If something wrong happens on the network layer, we still want to cache it and sync later.
            when {
                addToWatchList -> followedCache.insert(
                    Followed_shows(
                        show_id = showId,
                        synced = false
                    )
                )
                else -> followedCache.removeShow(showId)
            }
            Logger.withTag("observeUpdateFollowedShow").e(it.resolveError())
        },
        coroutineDispatcher = dispatcher
    )

    private suspend fun getOrCreateTraktList(
        user: Trakt_user
    ): Long {
        val traktFollowedList = favoriteCache.getFavoriteList()

        return if (traktFollowedList == null) {
            val listResponse = traktService.createFavoriteList(user.slug)
            listResponse.ids.trakt.toLong()
        } else {
            traktFollowedList.id
        }
    }

    override fun observeFollowedShows(): Flow<List<SelectFollowedShows>> =
        followedCache.observeFollowedShows()

    override fun observeFollowedShow(showId: Long): Flow<Boolean> =
        followedCache.observeFollowedShow(showId)
            .map { it?.synced ?: false }

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
                id = ids.trakt.toLong(),
                slug = ids.slug,
                description = description
            )
        )
    }

    private fun TraktPersonalListsResponse.mapAndCache() {
        favoriteCache.insert(
            Trakt_favorite_list(
                id = ids.trakt.toLong(),
                slug = ids.slug,
                description = description
            )
        )
    }

    private fun List<TraktFollowedShowResponse>.mapAndCache() {
        map {
            followedCache.insert(
                Followed_shows(
                    show_id = it.show.ids.tmdb.toLong(),
                    synced = false
                )
            )
        }
    }
}