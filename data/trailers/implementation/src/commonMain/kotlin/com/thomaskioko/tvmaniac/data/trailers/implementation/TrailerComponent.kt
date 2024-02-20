package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface TrailerComponent {

  @ApplicationScope @Provides fun provideTrailerDao(bind: TrailerDaoImpl): TrailerDao = bind

  @ApplicationScope
  @Provides
  fun provideTrailerRepository(bind: TrailerRepositoryImpl): TrailerRepository = bind
}
