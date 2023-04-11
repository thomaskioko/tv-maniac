package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbNetworkComponent
import me.tatarka.inject.annotations.Provides

interface TmdbNetworkPlatformComponent {

    @Provides
    fun provideTmdbNetworkComponent(bind: TmdbAndroidNetworkComponent): TmdbNetworkComponent = bind

}