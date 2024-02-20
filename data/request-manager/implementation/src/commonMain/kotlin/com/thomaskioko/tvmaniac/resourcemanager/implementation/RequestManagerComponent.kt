package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import me.tatarka.inject.annotations.Provides

interface RequestManagerComponent {

  @ApplicationScope
  @Provides
  fun provideRequestManagerRepository(
    bind: DefaultRequestManagerRepository,
  ): RequestManagerRepository = bind
}
