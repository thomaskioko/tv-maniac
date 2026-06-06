package com.thomaskioko.tvmaniac.watchstatus.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.ShowWatchStatusQueries
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchProgress
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowWatchStatusDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowWatchStatusDao {

    private val queries: ShowWatchStatusQueries
        get() = database.showWatchStatusQueries

    override fun upsert(showId: Id<ShowId>, status: WatchStatus, lastWatchedAt: Long?, lastSyncedAt: Long?) {
        queries.upsert(
            showId = showId,
            status = status,
            lastWatchedAt = lastWatchedAt,
            lastSyncedAt = lastSyncedAt,
        )
    }

    override fun getStatus(showId: Id<ShowId>): WatchStatus? =
        queries.statusForShow(showId).executeAsOneOrNull()?.status

    override fun observeStatus(showId: Id<ShowId>): Flow<WatchStatus?> =
        queries.statusForShow(showId)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { it?.status }

    override fun getWatchProgress(showId: Id<ShowId>): ShowWatchProgress? =
        queries.watchProgressForShow(showId)
            .executeAsOneOrNull()
            ?.let { ShowWatchProgress(watchedCount = it.watched_count, totalCount = it.total_count) }

    override fun delete(showId: Id<ShowId>) {
        queries.delete(showId)
    }

    override fun deleteAll() {
        queries.deleteAll()
    }
}
