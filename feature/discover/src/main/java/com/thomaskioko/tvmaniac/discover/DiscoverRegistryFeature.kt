package com.thomaskioko.tvmaniac.discover

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.thomaskioko.tvmaniac.common.navigation.Feature
import com.thomaskioko.tvmaniac.common.navigation.TvManiacScreens
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverRegistryFeature : Feature {
    override val screens: ScreenRegistry.() -> Unit = screenModule {
        register<TvManiacScreens.DiscoverScreen> {
            DiscoverScreen
        }
    }
}