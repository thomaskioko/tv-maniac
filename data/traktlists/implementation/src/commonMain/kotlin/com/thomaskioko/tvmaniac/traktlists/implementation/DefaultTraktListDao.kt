package com.thomaskioko.tvmaniac.traktlists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListDao {

    override fun observeAll(): Flow<List<TraktListEntity>> =
        database.traktListsQueries.selectAllWithPosters()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.groupBy { it.id }
                    .map { (_, group) ->
                        val list = group.first()
                        TraktListEntity(
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

    override fun upsert(entity: TraktListEntity) {
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
