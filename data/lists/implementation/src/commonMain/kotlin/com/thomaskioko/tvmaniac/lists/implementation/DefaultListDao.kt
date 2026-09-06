package com.thomaskioko.tvmaniac.lists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.lists.api.ListDao
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
        database.traktListsQueries.selectAll()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { list ->
                    UserListEntity(
                        id = list.id,
                        slug = list.slug,
                        name = list.name,
                        description = list.description,
                        itemCount = list.item_count,
                        createdAt = list.created_at,
                    )
                }
            }

    override fun observeListsWithPosters(): Flow<List<UserListEntity>> =
        database.traktListsQueries.selectAllWithPosters()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.groupBy { it.id }
                    .map { (_, group) ->
                        val list = group.first()
                        UserListEntity(
                            id = list.id,
                            slug = list.slug,
                            name = list.name,
                            description = list.description,
                            itemCount = list.item_count,
                            createdAt = list.created_at,
                            posterPaths = group.mapNotNull { it.poster_url }.take(4),
                        )
                    }
            }

    override fun upsert(entity: UserListEntity) {
        database.traktListsQueries.upsert(
            id = entity.id,
            slug = entity.slug,
            name = entity.name,
            description = entity.description,
            item_count = entity.itemCount,
            created_at = entity.createdAt,
        )
    }

    override fun deleteAll() {
        database.traktListsQueries.deleteAll()
    }
}
