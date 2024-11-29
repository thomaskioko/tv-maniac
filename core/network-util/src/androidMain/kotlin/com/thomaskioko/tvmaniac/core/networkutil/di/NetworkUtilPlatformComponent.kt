package com.thomaskioko.tvmaniac.core.networkutil.di

import com.thomaskioko.tvmaniac.core.networkutil.AndroidNetworkExceptionHandlerUtil
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
actual interface NetworkUtilPlatformComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideNetworkExceptionHandler(
    bind: AndroidNetworkExceptionHandlerUtil,
  ): NetworkExceptionHandler = bind
}
