package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.db.WatchProvidersByTraktId
import com.thomaskioko.tvmaniac.db.Watch_providers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchProviderDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatcher: AppCoroutineDispatchers,
) : WatchProviderDao {

    override fun upsert(entity: Watch_providers) {
        database.watchProvidersQueries.upsert(
            id = entity.id,
            tmdb_id = entity.tmdb_id,
            show_id = entity.show_id,
            logo_path = entity.logo_path,
            name = entity.name,
        )
    }

    override fun fetchWatchProviders(tmdbId: Long): List<WatchProviders> =
        database.watchProvidersQueries.watchProviders(Id(tmdbId))
            .executeAsList()
            .dedupedByBrand { it.name }

    override fun observeWatchProviders(tmdbId: Long): Flow<List<WatchProviders>> =
        database.watchProvidersQueries.watchProviders(Id(tmdbId))
            .asFlow()
            .mapToList(dispatcher.io)
            .map { rows -> rows.dedupedByBrand { it.name } }

    override fun observeWatchProvidersByTraktId(traktId: Long): Flow<List<WatchProvidersByTraktId>> {
        val showId = showIdResolver.showIdForTraktId(traktId) ?: return flowOf(emptyList())
        return database.watchProvidersQueries.watchProvidersByTraktId(showId)
            .asFlow()
            .mapToList(dispatcher.io)
            .map { rows -> rows.dedupedByBrand { it.name } }
    }

    override fun deleteByTraktId(traktId: Long) {
        val showId = showIdResolver.showIdForTraktId(traktId) ?: return
        database.watchProvidersQueries.deleteByShowId(showId)
    }

    override fun deleteAll() {
        database.transaction { database.watchProvidersQueries.deleteAll() }
    }
}
