package com.thomaskioko.tvmaniac.resourcemanager.implementation

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface RequestManagerComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideRequestManagerRepository(
    bind: DefaultRequestManagerRepository,
  ): RequestManagerRepository = bind
}
