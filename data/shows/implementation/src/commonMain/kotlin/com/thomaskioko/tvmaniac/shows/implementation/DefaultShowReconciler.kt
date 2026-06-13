package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.ReconciliationResult
import com.thomaskioko.tvmaniac.shows.api.ShowReconciler
import com.thomaskioko.tvmaniac.shows.api.ShowResolveOutcome
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowReconciler(
    private val tmdbDataSource: TmdbShowsNetworkDataSource,
    private val database: TvManiacDatabase,
    private val logger: Logger,
) : ShowReconciler {

    override suspend fun reconcile(
        tmdbId: Long?,
        imdbId: String?,
        title: String?,
        providerShowId: String?,
        provider: AccountProvider,
        result: ReconciliationResult,
    ): Pair<ShowResolveOutcome, ReconciliationResult> {
        val resolvedTmdbId = when {
            tmdbId != null -> tmdbId
            imdbId != null -> resolveViaImdb(imdbId) ?: return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
            else -> return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
        }

        ensureShowStub(
            tmdbId = resolvedTmdbId,
            title = title,
            providerShowId = providerShowId,
            provider = provider,
        )

        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(resolvedTmdbId)).executeAsOneOrNull()
            ?: return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)

        return ShowResolveOutcome.Resolved(resolvedTmdbId) to result.copy(matched = result.matched + 1)
    }

    private suspend fun resolveViaImdb(imdbId: String): Long? =
        when (val response = tmdbDataSource.findShowByExternalId(externalId = imdbId, source = "imdb_id")) {
            is ApiResponse.Success -> {
                if (response.body == null) logger.debug(TAG, "TMDB find returned null for imdb_id=$imdbId")
                response.body
            }
            else -> {
                logger.debug(TAG, "TMDB find failed for imdb_id=$imdbId")
                null
            }
        }

    private fun ensureShowStub(
        tmdbId: Long,
        title: String?,
        providerShowId: String?,
        provider: AccountProvider,
    ) {
        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOneOrNull()
            ?: run {
                database.tvShowQueries.upsert(
                    tmdb_id = Id<TmdbId>(tmdbId),
                    name = title ?: "",
                    overview = "",
                    language = null,
                    year = null,
                    ratings = 0.0,
                    vote_count = 0,
                    genres = null,
                    status = null,
                    episode_numbers = null,
                    season_numbers = null,
                    poster_path = null,
                    backdrop_path = null,
                )
                database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOneOrNull() ?: return
            }

        if (providerShowId != null) {
            database.tvshowExternalIdQueries.insert(
                showId = showId,
                provider = provider.toDbProvider(),
                externalId = providerShowId,
            )
        }
    }

    private companion object {
        private const val TAG = "ShowReconciler"
    }
}

private fun AccountProvider.toDbProvider(): Provider = when (this) {
    AccountProvider.TRAKT -> Provider.TRAKT
    AccountProvider.SIMKL -> Provider.SIMKL
}
