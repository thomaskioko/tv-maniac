package com.thomaskioko.tvmaniac.common.navigation.inject

import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.thomaskioko.tvmaniac.common.navigation.Feature
import com.thomaskioko.tvmaniac.util.AppInitializer
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmSuppressWildcards

@Inject
class FeatureRegistryInitializer(
    private val features: Set<@JvmSuppressWildcards Feature>,
) : AppInitializer {

    override fun init() {
        ScreenRegistry {
            features.forEach { it.screens(this) }
        }
    }
}
