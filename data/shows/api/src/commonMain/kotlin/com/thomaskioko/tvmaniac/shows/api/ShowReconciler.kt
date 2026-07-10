package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

public sealed class ShowResolveOutcome {
    public data class Resolved(public val tmdbId: Long) : ShowResolveOutcome()
    public data object Skipped : ShowResolveOutcome()
}

public data class ReconciliationResult(
    public val matched: Int,
    public val tmdbMissing: Int,
)

public interface ShowReconciler {
    public suspend fun reconcile(
        tmdbId: Long?,
        imdbId: String?,
        title: String?,
        providerShowId: String?,
        provider: SyncProviderSource,
        result: ReconciliationResult = ReconciliationResult(matched = 0, tmdbMissing = 0),
    ): Pair<ShowResolveOutcome, ReconciliationResult>
}
