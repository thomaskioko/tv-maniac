package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface SeasonDetailsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideSeasonDetailsRepository(
    bind: DefaultSeasonDetailsRepository,
  ): SeasonDetailsRepository = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideSeasonDetailsDa(bind: DefaultSeasonDetailsDao): SeasonDetailsDao = bind
}
