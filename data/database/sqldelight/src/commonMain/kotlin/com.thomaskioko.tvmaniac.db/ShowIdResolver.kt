package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

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
