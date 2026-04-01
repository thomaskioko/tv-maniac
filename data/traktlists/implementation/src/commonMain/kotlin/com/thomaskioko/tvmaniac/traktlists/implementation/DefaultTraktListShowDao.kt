package com.thomaskioko.tvmaniac.traktlists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListShowEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListShowDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListShowDao {

    override fun observeActiveCountByListId(): Flow<Map<Long, Long>> =
        database.traktListShowsQueries.countActiveByListId()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.associate { it.list_id to it.show_count } }

    override fun observeByShowTraktId(showTraktId: Long): Flow<List<TraktListShowEntry>> =
        database.traktListShowsQueries.selectByShowTraktId(showTraktId)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    TraktListShowEntry(
                        listId = row.list_id,
                        showTraktId = row.show_trakt_id,
                        listedAt = row.listed_at,
                        pendingAction = row.pending_action,
                    )
                }
            }

    override fun upsert(listId: Long, showTraktId: Long, listedAt: String, pendingAction: String) {
        database.traktListShowsQueries.upsert(
            list_id = listId,
            show_trakt_id = showTraktId,
            listed_at = listedAt,
            pending_action = pendingAction,
        )
    }

    override fun updatePendingAction(listId: Long, showTraktId: Long, pendingAction: String) {
        database.traktListShowsQueries.updatePendingAction(
            pending_action = pendingAction,
            list_id = listId,
            show_trakt_id = showTraktId,
        )
    }

    override fun deleteByListIdAndShowId(listId: Long, showTraktId: Long) {
        database.traktListShowsQueries.deleteByListIdAndShowId(
            list_id = listId,
            show_trakt_id = showTraktId,
        )
    }
}
