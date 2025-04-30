package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSeasonsRepository(
  private val seasonsDao: SeasonsDao,
) : SeasonsRepository {

  override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> =
    seasonsDao.observeSeasonsByShowId(id)
}
