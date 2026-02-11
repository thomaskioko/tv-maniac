package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.WatchProvidersByTraktId
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
            tmdb_id = entity.tmdb_id,
            trakt_id = entity.trakt_id,
            logo_path = entity.logo_path,
            name = entity.name,
        )
    }

    override fun fetchWatchProviders(id: Long): List<WatchProviders> =
        database.watchProvidersQueries.watchProviders(Id(id)).executeAsList()

    override fun observeWatchProviders(id: Long): Flow<List<WatchProviders>> =
        database.watchProvidersQueries.watchProviders(Id(id)).asFlow().mapToList(dispatcher.io)

    override fun observeWatchProvidersByTraktId(traktId: Long): Flow<List<WatchProvidersByTraktId>> =
        database.watchProvidersQueries.watchProvidersByTraktId(Id<TraktId>(traktId))
            .asFlow()
            .mapToList(dispatcher.io)

    override fun fetchWatchProvidersByTraktId(traktId: Long): List<WatchProvidersByTraktId> =
        database.watchProvidersQueries.watchProvidersByTraktId(Id<TraktId>(traktId)).executeAsList()

    override fun deleteByTraktId(traktId: Long) {
        database.watchProvidersQueries.deleteByTraktId(Id<TraktId>(traktId))
    }

    override fun deleteAll() {
        database.transaction { database.watchProvidersQueries.deleteAll() }
    }
}
