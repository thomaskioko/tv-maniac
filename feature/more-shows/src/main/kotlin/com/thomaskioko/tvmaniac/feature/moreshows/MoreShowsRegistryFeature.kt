package com.thomaskioko.tvmaniac.feature.moreshows

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.thomaskioko.tvmaniac.common.navigation.Feature
import com.thomaskioko.tvmaniac.common.navigation.TvManiacScreens
import me.tatarka.inject.annotations.Inject

@Inject
class MoreShowsRegistryFeature : Feature {
    override val screens: ScreenRegistry.() -> Unit = screenModule {
        register<TvManiacScreens.MoreShowsScreen> {
            MoreShowsScreen
        }
    }
}
