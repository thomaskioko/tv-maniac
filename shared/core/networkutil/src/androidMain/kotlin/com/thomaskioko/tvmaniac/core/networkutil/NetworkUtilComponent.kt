package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.core.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface NetworkUtilComponent {

    @ApplicationScope
    @Provides
    fun provideNetworkUtil(bind: NetworkUtilImpl): NetworkUtil = bind
}