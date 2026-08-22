package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ShowTraktIdResolver
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class TraktShowIdResolver(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val accountManager: AccountManager,
    private val database: TvManiacDatabase,
    private val logger: Logger,
) : ShowTraktIdResolver {

    override suspend fun resolveMissingTraktIds(tmdbIds: List<Long>): Int {
        if (accountManager.getActiveProvider() != SyncProviderSource.TRAKT) return 0

        var resolved = 0
        for (tmdbId in tmdbIds) {
            val showId = database.tvShowQueries
                .getShowIdByTmdbId(Id(tmdbId))
                .executeAsOneOrNull()
                ?: continue

            val existing = database.tvshowExternalIdQueries
                .externalIdForShow(showId = showId, provider = Provider.TRAKT)
                .executeAsOneOrNull()
            if (existing != null) continue

            val traktId = lookupTraktId(tmdbId) ?: continue

            database.tvshowExternalIdQueries.insert(
                showId = showId,
                provider = Provider.TRAKT,
                externalId = traktId.toString(),
            )
            resolved++
        }
        return resolved
    }

    private suspend fun lookupTraktId(tmdbId: Long): Long? =
        when (val response = remoteDataSource.getShowByTmdbId(tmdbId)) {
            is ApiResponse.Success -> response.body.firstNotNullOfOrNull { it.show?.ids?.trakt }
            else -> {
                logger.debug(TAG, "Trakt id lookup failed for tmdb $tmdbId")
                null
            }
        }

    private companion object {
        private const val TAG = "TraktShowIdResolver"
    }
}
