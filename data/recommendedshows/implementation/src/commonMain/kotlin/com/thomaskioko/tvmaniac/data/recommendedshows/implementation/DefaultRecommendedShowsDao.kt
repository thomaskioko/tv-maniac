package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
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
