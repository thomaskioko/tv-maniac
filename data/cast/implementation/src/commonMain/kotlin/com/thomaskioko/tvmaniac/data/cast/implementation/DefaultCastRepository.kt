package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.core.db.SeasonCast
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultCastRepository(
  private val dao: CastDao,
) : CastRepository {

  override suspend fun fetchSeasonCast(seasonId: Long): List<SeasonCast> =
    dao.fetchSeasonCast(seasonId)

  override suspend fun fetchShowCast(showId: Long): List<ShowCast> = dao.fetchShowCast(showId)

  override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
    dao.observeSeasonCast(seasonId)

  override fun observeShowCast(showId: Long): Flow<List<ShowCast>> = dao.observeShowCast(showId)
}
