package com.thomaskioko.tvmaniac.data.trailers.implementation

import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface TrailerComponent {

  @SingleIn(AppScope::class)
  @Provides fun provideTrailerDao(bind: TrailerDaoImpl): TrailerDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTrailerRepository(bind: DefaultTrailerRepository): TrailerRepository = bind
}
