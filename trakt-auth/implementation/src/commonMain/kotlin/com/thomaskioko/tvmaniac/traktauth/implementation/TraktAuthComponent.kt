package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface TraktAuthComponent {
  @Provides
  @SingleIn(AppScope::class)
  fun provideTraktAuthRepository(bind: DefaultTraktAuthRepository): TraktAuthRepository = bind
}

