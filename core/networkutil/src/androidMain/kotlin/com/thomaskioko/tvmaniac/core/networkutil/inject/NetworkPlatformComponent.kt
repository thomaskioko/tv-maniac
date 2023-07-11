package com.thomaskioko.tvmaniac.core.networkutil.inject

import com.thomaskioko.tvmaniac.core.networkutil.AndroidNetworkExceptionHandlerUtil
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface NetworkPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideNetworkExceptionHandler(
        bind: AndroidNetworkExceptionHandlerUtil,
    ): NetworkExceptionHandler = bind
}
