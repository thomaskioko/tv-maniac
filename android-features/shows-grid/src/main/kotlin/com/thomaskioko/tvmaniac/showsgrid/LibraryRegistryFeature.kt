package com.thomaskioko.tvmaniac.showsgrid

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.thomaskioko.tvmaniac.common.navigation.Feature
import com.thomaskioko.tvmaniac.common.navigation.SharedScreen
import me.tatarka.inject.annotations.Inject

@Inject
class LibraryRegistryFeature : Feature {
    override val screens: ScreenRegistry.() -> Unit = screenModule {
        register<SharedScreen.LibraryScreen> {
            LibraryScreen
        }
    }
}
