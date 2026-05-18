package com.thomaskioko.tvmaniac.watchedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowEntry
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedShowsDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchedShowsDao {

    override fun entries(): List<WatchedShowEntry> =
        database.traktWatchedShowsQueries.entries().executeAsList().map { row ->
            WatchedShowEntry(
                traktId = row.trakt_id.id,
                tmdbId = row.tmdb_id?.id,
                plays = row.plays,
                lastWatchedAt = row.last_watched_at,
                lastUpdatedAt = row.last_updated_at,
            )
        }

    override fun entriesObservable(): Flow<List<WatchedShowEntry>> =
        database.traktWatchedShowsQueries.entriesObservable()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    WatchedShowEntry(
                        traktId = row.trakt_id.id,
                        tmdbId = row.tmdb_id?.id,
                        plays = row.plays,
                        lastWatchedAt = row.last_watched_at,
                        lastUpdatedAt = row.last_updated_at,
                    )
                }
            }

    override fun traktIdsMissingShowDetails(): List<Long> =
        database.traktWatchedShowsQueries.traktIdsMissingShowDetails()
            .executeAsList()
            .map { it.id }

    override fun upsert(entry: WatchedShowEntry) {
        database.traktWatchedShowsQueries.upsert(
            traktId = Id(entry.traktId),
            tmdbId = entry.tmdbId?.let { Id(it) },
            plays = entry.plays,
            lastWatchedAt = entry.lastWatchedAt,
            lastUpdatedAt = entry.lastUpdatedAt,
        )
    }

    override fun deleteByTraktId(traktId: Long) {
        database.traktWatchedShowsQueries.deleteByTraktId(Id(traktId))
    }

    override fun deleteAll() {
        database.traktWatchedShowsQueries.deleteAll()
    }
}
