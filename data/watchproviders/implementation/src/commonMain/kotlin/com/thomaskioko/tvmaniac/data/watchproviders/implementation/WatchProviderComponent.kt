package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
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
