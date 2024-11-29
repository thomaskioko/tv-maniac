package com.thomaskioko.tvmaniac.data.cast.implementation

import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface CastComponent {

  @SingleIn(AppScope::class)
  @Provides fun provideCastDao(bind: DefaultCastDao): CastDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideCastRepository(bind: DefaultCastRepository): CastRepository = bind
}
