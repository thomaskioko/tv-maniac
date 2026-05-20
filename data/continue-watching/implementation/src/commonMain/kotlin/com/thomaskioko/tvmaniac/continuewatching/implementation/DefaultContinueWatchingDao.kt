package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ContinueWatchingDao {

    override fun entries(): List<ContinueWatchingEntry> =
        database.traktContinueWatchingQueries.entries().executeAsList().map { row ->
            ContinueWatchingEntry(
                traktId = row.trakt_id.id,
                tmdbId = row.tmdb_id?.id,
                airedEpisodes = row.aired_episodes,
                completedCount = row.completed_count,
                lastWatchedAt = row.last_watched_at,
                lastUpdatedAt = row.last_updated_at,
                title = row.title,
                year = row.year,
            )
        }

    override fun entriesObservable(): Flow<List<ContinueWatchingEntry>> =
        database.traktContinueWatchingQueries.entriesObservable()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    ContinueWatchingEntry(
                        traktId = row.trakt_id.id,
                        tmdbId = row.tmdb_id?.id,
                        airedEpisodes = row.aired_episodes,
                        completedCount = row.completed_count,
                        lastWatchedAt = row.last_watched_at,
                        lastUpdatedAt = row.last_updated_at,
                        title = row.title,
                        year = row.year,
                    )
                }
            }

    override fun traktIdsMissingShowDetails(): List<Long> =
        database.traktContinueWatchingQueries.traktIdsMissingShowDetails()
            .executeAsList()
            .map { it.id }

    override fun upsert(entry: ContinueWatchingEntry) {
        database.traktContinueWatchingQueries.upsert(
            traktId = Id(entry.traktId),
            tmdbId = entry.tmdbId?.let { Id(it) },
            airedEpisodes = entry.airedEpisodes,
            completedCount = entry.completedCount,
            lastWatchedAt = entry.lastWatchedAt,
            lastUpdatedAt = entry.lastUpdatedAt,
            title = entry.title,
            year = entry.year,
        )
    }

    override fun deleteByTraktId(traktId: Long) {
        database.traktContinueWatchingQueries.deleteByTraktId(Id(traktId))
    }

    override fun deleteAll() {
        database.traktContinueWatchingQueries.deleteAll()
    }
}
