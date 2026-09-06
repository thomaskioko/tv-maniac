package com.thomaskioko.tvmaniac.lists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.lists.api.ListDao
import com.thomaskioko.tvmaniac.lists.api.PendingUploadList
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultListDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ListDao {

    override fun observeAll(): Flow<List<UserListEntity>> =
        database.listsQueries.selectAll()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { list ->
                    UserListEntity(
                        id = list.id,
                        traktId = list.trakt_id,
                        slug = list.slug,
                        name = list.name,
                        description = list.description,
                        itemCount = list.item_count,
                        createdAt = list.created_at,
                    )
                }
            }

    override fun observeListsWithPosters(): Flow<List<UserListEntity>> =
        database.listsQueries.selectAllWithPosters()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.groupBy { it.id }
                    .map { (_, group) ->
                        val list = group.first()
                        UserListEntity(
                            id = list.id,
                            traktId = list.trakt_id,
                            slug = list.slug,
                            name = list.name,
                            description = list.description,
                            itemCount = list.item_count,
                            createdAt = list.created_at,
                            posterPaths = group.mapNotNull { it.poster_url }.take(4),
                        )
                    }
            }

    override fun upsertByTraktId(entity: UserListEntity) {
        database.listsQueries.upsertByTraktId(
            trakt_id = entity.traktId,
            slug = entity.slug,
            name = entity.name,
            description = entity.description,
            item_count = entity.itemCount,
            created_at = entity.createdAt,
        )
    }

    override fun insertLocal(name: String, createdAt: String): Long {
        database.listsQueries.insertLocal(name = name, createdAt = createdAt)
        return database.listsQueries.lastInsertRowId().executeAsOne()
    }

    override fun markSynced(id: Long, traktId: Long, slug: String?) {
        database.listsQueries.markSynced(traktId = traktId, slug = slug, id = id)
    }

    override fun getTraktId(id: Long): Long? =
        database.listsQueries.selectTraktIdById(id).executeAsOneOrNull()?.trakt_id

    override fun selectIdsByTraktId(): Map<Long, Long> =
        database.listsQueries.selectSyncedIds().executeAsList()
            .associate { requireNotNull(it.trakt_id) to it.id }

    override fun selectPendingUploadLists(): List<PendingUploadList> =
        database.listsQueries.selectPendingUploadLists().executeAsList()
            .map { PendingUploadList(id = it.id, name = it.name) }

    override fun countPendingUploads(): Long =
        database.listsQueries.countPendingUploads().executeAsOne()

    override fun deleteById(id: Long) {
        database.listsQueries.deleteById(id)
    }

    override fun deleteAll() {
        database.listsQueries.deleteAll()
    }
}
