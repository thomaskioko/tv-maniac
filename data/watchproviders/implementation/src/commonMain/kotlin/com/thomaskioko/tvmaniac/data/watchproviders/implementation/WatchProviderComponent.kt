package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import me.tatarka.inject.annotations.Provides

interface WatchProviderComponent {

  @ApplicationScope
  @Provides
  fun provideWatchProviderDao(bind: DefaultWatchProviderDao): WatchProviderDao = bind

  @ApplicationScope
  @Provides
  fun provideWatchProviderRepository(
    bind: DefaultWatchProviderRepository,
  ): WatchProviderRepository = bind
}
