package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.db.Watch_providers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.Id
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultWatchProviderDao(
  private val database: TvManiacDatabase,
  private val dispatcher: AppCoroutineDispatchers,
) : WatchProviderDao {

  override fun upsert(entity: Watch_providers) {
    database.watch_providersQueries.upsert(
      id = entity.id,
      name = entity.name,
      logo_path = entity.logo_path,
      tmdb_id = entity.tmdb_id,
    )
  }

  override fun fetchWatchProviders(id: Long): List<WatchProviders> =
    database.watch_providersQueries.watchProviders(Id(id)).executeAsList()

  override fun observeWatchProviders(id: Long): Flow<List<WatchProviders>> =
    database.watch_providersQueries.watchProviders(Id(id)).asFlow().mapToList(dispatcher.io)

  override fun delete(id: Long) {
    database.watch_providersQueries.delete(Id(id))
  }

  override fun deleteAll() {
    database.transaction { database.watch_providersQueries.deleteAll() }
  }
}
