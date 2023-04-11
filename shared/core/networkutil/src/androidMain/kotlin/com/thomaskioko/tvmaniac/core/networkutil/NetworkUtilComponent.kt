package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface NetworkUtilComponent {

    @ApplicationScope
    @Provides
    fun provideNetworkUtil(bind: NetworkRepositoryImpl): NetworkRepository = bind
}