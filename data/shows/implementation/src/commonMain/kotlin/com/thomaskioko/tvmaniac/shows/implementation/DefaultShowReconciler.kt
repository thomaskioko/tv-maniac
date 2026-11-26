package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.toDbProvider
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.Id
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
        provider: SyncProviderSource,
        result: ReconciliationResult,
    ): Pair<ShowResolveOutcome, ReconciliationResult> {
        val resolvedTmdbId = when {
            tmdbId != null -> tmdbId
            imdbId != null -> when (val lookup = resolveViaImdb(imdbId)) {
                is ImdbLookup.Found -> lookup.tmdbId
                ImdbLookup.NoMatch -> return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
                ImdbLookup.Failed -> return ShowResolveOutcome.Failed to result.copy(tmdbMissing = result.tmdbMissing + 1)
            }
            else -> return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
        }

        ensureShowStub(
            tmdbId = resolvedTmdbId,
            title = title,
            providerShowId = providerShowId,
            provider = provider,
        )

        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(resolvedTmdbId)).executeAsOneOrNull()
            ?: return ShowResolveOutcome.Failed to result.copy(tmdbMissing = result.tmdbMissing + 1)

        return ShowResolveOutcome.Resolved(resolvedTmdbId) to result.copy(matched = result.matched + 1)
    }

    private suspend fun resolveViaImdb(imdbId: String): ImdbLookup =
        when (val response = tmdbDataSource.findShowByExternalId(externalId = imdbId, source = "imdb_id")) {
            is ApiResponse.Success -> {
                val tmdbId = response.body
                if (tmdbId == null) {
                    logger.debug(TAG, "TMDB find returned null for imdb_id=$imdbId")
                    ImdbLookup.NoMatch
                } else {
                    ImdbLookup.Found(tmdbId)
                }
            }
            else -> {
                logger.debug(TAG, "TMDB find failed for imdb_id=$imdbId")
                ImdbLookup.Failed
            }
        }

    private fun ensureShowStub(
        tmdbId: Long,
        title: String?,
        providerShowId: String?,
        provider: SyncProviderSource,
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

    private sealed interface ImdbLookup {
        data class Found(val tmdbId: Long) : ImdbLookup
        data object NoMatch : ImdbLookup
        data object Failed : ImdbLookup
    }

    private companion object {
        private const val TAG = "ShowReconciler"
    }
}
