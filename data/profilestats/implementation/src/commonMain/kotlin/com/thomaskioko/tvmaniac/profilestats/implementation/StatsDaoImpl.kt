package com.thomaskioko.tvmaniac.profilestats.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Stats
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.profilestats.api.StatsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class StatsDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : StatsDao {

    override fun insert(stats: Stats) {
        database.statsQueries.insertOrReplace(
            slug = stats.slug,
            months = stats.months,
            days = stats.days,
            hours = stats.hours,
            collected_shows = stats.collected_shows,
            episodes_watched = stats.episodes_watched,
        )
    }

    override fun observeStats(slug: String): Flow<Stats> {
        return database.statsQueries.select(slug)
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override fun delete(slug: String) {
        database.statsQueries.delete(slug)
    }

    override fun deleteAll() {
        database.transaction {
            database.statsQueries.deleteAll()
        }
    }
}
