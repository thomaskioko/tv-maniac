package com.thomaskioko.tvmaniac.home.nav.di.model

import kotlinx.serialization.Serializable

@Serializable
public sealed interface HomeConfig {
    @Serializable
    public data object Discover : HomeConfig

    @Serializable
    public data object Progress : HomeConfig

    @Serializable
    public data object Library : HomeConfig

    @Serializable
    public data object Profile : HomeConfig
}
