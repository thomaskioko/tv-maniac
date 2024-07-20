package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import me.tatarka.inject.annotations.Provides

interface SeasonsComponent {

  @ApplicationScope
  @Provides
  fun provideSeasonsRepository(bind: DefaultSeasonsRepository): SeasonsRepository = bind

  @ApplicationScope @Provides fun provideSeasonsDao(bind: SeasonsDaoImpl): SeasonsDao = bind
}
