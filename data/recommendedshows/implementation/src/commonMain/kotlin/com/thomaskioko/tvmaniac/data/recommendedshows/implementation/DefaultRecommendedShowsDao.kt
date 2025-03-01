package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultRecommendedShowsDao(
  private val database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : RecommendedShowsDao {
  override fun upsert(showId: Long, recommendedShowId: Long) {
    database.recommended_showsQueries.transaction {
      database.recommended_showsQueries.upsert(
        id = Id(recommendedShowId),
        recommended_show_id = Id(showId),
      )
    }
  }

  override fun observeRecommendedShows(traktId: Long): Flow<List<RecommendedShows>> {
    return database.recommended_showsQueries
      .recommendedShows(Id(traktId))
      .asFlow()
      .mapToList(dispatchers.io)
  }

  override fun delete(id: Long) {
    database.similar_showsQueries.delete(Id(id))
  }

  override fun deleteAll() {
    database.transaction { database.similar_showsQueries.deleteAll() }
  }
}
