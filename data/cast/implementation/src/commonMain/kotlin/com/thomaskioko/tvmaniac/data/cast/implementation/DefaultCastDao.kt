package com.thomaskioko.tvmaniac.data.cast.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Id
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultCastDao(
  private val database: TvManiacDatabase,
  private val dispatcher: AppCoroutineDispatchers,
) : CastDao {
  override fun upsert(entity: Casts) {
    database.castQueries.upsert(
      id = entity.id,
      season_id = entity.season_id,
      tmdb_id = entity.tmdb_id,
      name = entity.name,
      character_name = entity.character_name,
      profile_path = entity.profile_path,
      popularity = entity.popularity,
    )
  }

  override fun fetchShowCast(id: Long): List<ShowCast> =
    database.castQueries.showCast(Id(id)).executeAsList()

  override fun observeShowCast(id: Long): Flow<List<ShowCast>> =
    database.castQueries.showCast(Id(id)).asFlow().mapToList(dispatcher.io)

  override fun fetchSeasonCast(id: Long): List<SeasonCast> =
    database.castQueries.seasonCast(Id(id)).executeAsList()

  override fun observeSeasonCast(id: Long): Flow<List<SeasonCast>> =
    database.castQueries.seasonCast(Id(id)).asFlow().mapToList(dispatcher.io)
}
