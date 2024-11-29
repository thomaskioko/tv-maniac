package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface WatchProviderComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideWatchProviderDao(bind: DefaultWatchProviderDao): WatchProviderDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideWatchProviderRepository(
    bind: DefaultWatchProviderRepository,
  ): WatchProviderRepository = bind
}
