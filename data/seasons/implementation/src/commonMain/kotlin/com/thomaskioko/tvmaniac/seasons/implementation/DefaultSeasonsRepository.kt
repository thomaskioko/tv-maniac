package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultSeasonsRepository(
  private val seasonsDao: SeasonsDao,
  private val dispatcher: AppCoroutineDispatchers,
) : SeasonsRepository {

  override fun observeSeasonsByShowId(id: Long): Flow<Either<Failure, List<ShowSeasons>>> =
    seasonsDao
      .observeSeasonsByShowId(id)
      .map { seasons -> Either.Right(seasons) }
      .distinctUntilChanged()
      .flowOn(dispatcher.io)
}
