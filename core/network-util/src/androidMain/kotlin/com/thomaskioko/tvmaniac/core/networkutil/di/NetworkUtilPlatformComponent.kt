package com.thomaskioko.tvmaniac.core.networkutil.di

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.networkutil.AndroidNetworkExceptionHandlerUtil
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import me.tatarka.inject.annotations.Provides

actual interface NetworkUtilPlatformComponent {

  @ApplicationScope
  @Provides
  fun provideNetworkExceptionHandler(
    bind: AndroidNetworkExceptionHandlerUtil,
  ): NetworkExceptionHandler = bind
}
