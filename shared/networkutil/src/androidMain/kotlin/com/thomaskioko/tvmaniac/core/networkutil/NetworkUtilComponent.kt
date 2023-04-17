package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface NetworkUtilComponent {

    @ApplicationScope
    @Provides
    fun provideNetworkUtil(bind: NetworkRepositoryImpl): NetworkRepository = bind
}