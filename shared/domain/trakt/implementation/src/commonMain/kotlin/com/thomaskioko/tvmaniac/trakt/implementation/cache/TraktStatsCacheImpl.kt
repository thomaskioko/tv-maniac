package com.thomaskioko.tvmaniac.trakt.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktStatsCache
import kotlinx.coroutines.flow.Flow

class TraktStatsCacheImpl(
    private val database: TvManiacDatabase
) : TraktStatsCache {

    override fun insert(stats: TraktStats) {
        database.traktStatsQueries.insertOrReplace(
            user_slug = stats.user_slug,
            months = stats.months,
            days = stats.days,
            hours = stats.hours,
            collected_shows = stats.collected_shows,
            episodes_watched = stats.episodes_watched
        )
    }

    override fun observeStats(): Flow<TraktStats?> {
        return database.traktStatsQueries.select()
            .asFlow()
            .mapToOneOrNull()
    }
}