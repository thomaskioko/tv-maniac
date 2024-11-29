package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.core.db.SeasonCast
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultCastRepository(
  private val dao: CastDao,
) : CastRepository {

  override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
    dao.observeSeasonCast(seasonId)

  override fun observeShowCast(showId: Long): Flow<List<ShowCast>> = dao.observeShowCast(showId)
}
