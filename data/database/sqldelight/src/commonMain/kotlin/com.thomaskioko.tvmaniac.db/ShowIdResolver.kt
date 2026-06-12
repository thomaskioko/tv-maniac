package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

public interface ShowIdResolver {
    public fun showIdForTmdbId(tmdbId: Long): Id<ShowId>?
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowIdResolver(
    database: TvManiacDatabase,
) : ShowIdResolver {

    private val queries = database.tvShowQueries

    override fun showIdForTmdbId(tmdbId: Long): Id<ShowId>? =
        queries.getShowIdByTmdbId(Id(tmdbId)).executeAsOneOrNull()
}
