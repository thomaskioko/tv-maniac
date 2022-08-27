package com.thomaskioko.tvmanic.trakt.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
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
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf

class TraktRepositoryImpl constructor(
    private val cache: TraktUserCache,
    private val followedCache: TraktFollowedCache,
    private val favoriteCache: TraktFavoriteListCache,
    private val traktService: TraktService,
    private val tmdbRepository: TmdbRepository,
    private val dispatcher: CoroutineDispatcher,
) : TraktRepository {

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> =
        networkBoundResource(
            query = { cache.observeMe() },
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
        getLocalTraktUser()?.let { user ->
            traktService.getUserList(user.slug)
                .firstOrNull()?.let {
                    observeFollowedShows(it.ids.trakt, user.slug).collect()
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

    override fun getLocalTraktUser(): Trakt_user? = cache.getMe()

    override fun getFavoriteList(): Trakt_favorite_list? = favoriteCache.getFavoriteList()

    private fun TraktUserResponse.mapAndCache(slug: String) {
        cache.insert(
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