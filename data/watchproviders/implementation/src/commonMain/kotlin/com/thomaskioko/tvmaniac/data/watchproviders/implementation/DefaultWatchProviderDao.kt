package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.Watch_providers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchProviderDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : WatchProviderDao {

    override fun upsert(entity: Watch_providers) {
        database.watchProvidersQueries.upsert(
            id = entity.id,
            name = entity.name,
            logo_path = entity.logo_path,
            tmdb_id = entity.tmdb_id,
        )
    }

    override fun fetchWatchProviders(id: Long): List<WatchProviders> =
        database.watchProvidersQueries.watchProviders(Id(id)).executeAsList()

    override fun observeWatchProviders(id: Long): Flow<List<WatchProviders>> =
        database.watchProvidersQueries.watchProviders(Id(id)).asFlow().mapToList(dispatcher.io)

    override fun delete(id: Long) {
        database.watchProvidersQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { database.watchProvidersQueries.deleteAll() }
    }
}
