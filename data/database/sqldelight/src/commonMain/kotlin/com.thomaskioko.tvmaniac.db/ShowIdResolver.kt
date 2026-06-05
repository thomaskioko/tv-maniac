package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Translates the data layer's public `traktId` boundary to the internal canonical `showId`.
 *
 * Phase 0 keeps `traktId` at the data layer's public surface and resolves it to `showId` internally
 * via the `TRAKT` rows in `tvshow_external_id`. Returns null when no show maps to the trakt id.
 */
public interface ShowIdResolver {
    public fun showIdForTraktId(traktId: Long): Id<ShowId>?
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowIdResolver(
    database: TvManiacDatabase,
) : ShowIdResolver {

    private val queries = database.tvshowExternalIdQueries

    override fun showIdForTraktId(traktId: Long): Id<ShowId>? =
        queries.showIdForExternalId(provider = Provider.TRAKT, externalId = traktId.toString())
            .executeAsOneOrNull()
}
