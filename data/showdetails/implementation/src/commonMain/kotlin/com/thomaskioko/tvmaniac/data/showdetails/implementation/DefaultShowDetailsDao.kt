package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultShowDetailsDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsDao {
  private val tvShowsQueries = database.tvshowsQueries

  override fun observeTvShows(id: Long): Flow<TvshowDetails> =
    tvShowsQueries.tvshowDetails(Id(id)).asFlow().mapToOne(dispatchers.io)

  override fun getTvShow(id: Long): TvshowDetails =
    tvShowsQueries.tvshowDetails(Id(id)).executeAsOne()

  override fun deleteTvShow(id: Long) {
    tvShowsQueries.delete(Id(id))
  }
}
