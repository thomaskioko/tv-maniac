package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowIdResolver
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
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : ContinueWatchingDao {

    override fun entries(): List<ContinueWatchingEntry> =
        database.continueWatchingQueries.entries().executeAsList().map { row ->
            ContinueWatchingEntry(
                showId = row.show_id.id,
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
        database.continueWatchingQueries.entriesObservable()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    ContinueWatchingEntry(
                        showId = row.show_id.id,
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

    override fun showIdsMissingShowDetails(): List<Long> =
        database.continueWatchingQueries.traktIdsMissingShowDetails()
            .executeAsList()

    override fun upsert(entry: ContinueWatchingEntry) {
        val showId = showIdResolver.showIdForTmdbId(entry.showId) ?: return
        database.continueWatchingQueries.upsert(
            showId = showId,
            tmdbId = entry.tmdbId?.let { Id(it) },
            airedEpisodes = entry.airedEpisodes,
            completedCount = entry.completedCount,
            lastWatchedAt = entry.lastWatchedAt,
            lastUpdatedAt = entry.lastUpdatedAt,
            title = entry.title,
            year = entry.year,
        )
    }

    override fun upsertPlaceholder(showId: Long, tmdbId: Long?, title: String?, year: Long?) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        database.continueWatchingQueries.upsertPlaceholder(
            showId = internalShowId,
            tmdbId = tmdbId?.let { Id(it) },
            title = title,
            year = year,
        )
    }

    override fun deleteByShowId(showId: Long) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        database.continueWatchingQueries.deleteByShowId(internalShowId)
    }

    override fun deleteAll() {
        database.continueWatchingQueries.deleteAll()
    }
}
