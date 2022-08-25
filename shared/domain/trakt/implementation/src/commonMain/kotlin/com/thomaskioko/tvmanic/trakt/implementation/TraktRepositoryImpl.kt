package com.thomaskioko.tvmanic.trakt.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.TraktUserCache
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class TraktRepositoryImpl constructor(
    private val cache: TraktUserCache,
    private val traktService: TraktService,
    private val dispatcher: CoroutineDispatcher,
) : TraktRepository {

    override fun observeMe(slug: String): Flow<Resource<Trakt_user>> =
        networkBoundResource(
            query = { cache.getMe() },
            shouldFetch = { true },
            fetch = { traktService.getUserProfile(slug) },
            saveFetchResult = { it.mapAndCache(slug) },
            onFetchFailed = { Logger.withTag("observeMe").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

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
}