package com.thomaskioko.tvmaniac.traktlists.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TraktListDao {

    override fun observeAll(): Flow<List<TraktListEntity>> =
        database.traktListsQueries.selectAll()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows ->
                rows.map { row ->
                    TraktListEntity(
                        id = row.id,
                        slug = row.slug,
                        name = row.name,
                        description = row.description,
                        itemCount = row.item_count,
                        createdAt = row.created_at,
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
