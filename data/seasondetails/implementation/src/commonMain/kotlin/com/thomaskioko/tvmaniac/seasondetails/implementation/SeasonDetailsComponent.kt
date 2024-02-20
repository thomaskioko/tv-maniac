package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import me.tatarka.inject.annotations.Provides

interface SeasonDetailsComponent {

  @ApplicationScope
  @Provides
  fun provideSeasonDetailsRepository(
    bind: DefaultSeasonDetailsRepository,
  ): SeasonDetailsRepository = bind

  @ApplicationScope
  @Provides
  fun provideSeasonDetailsDa(bind: DefaultSeasonDetailsDao): SeasonDetailsDao = bind
}
