package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface SeasonsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideSeasonsRepository(bind: DefaultSeasonsRepository): SeasonsRepository = bind

  @SingleIn(AppScope::class)
  @Provides fun provideSeasonsDao(bind: SeasonsDaoImpl): SeasonsDao = bind
}
