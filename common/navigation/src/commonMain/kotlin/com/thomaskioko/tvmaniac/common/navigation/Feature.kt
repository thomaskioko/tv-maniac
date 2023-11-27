package com.thomaskioko.tvmaniac.common.navigation

import cafe.adriel.voyager.core.registry.ScreenRegistry

interface Feature {
    val screens: ScreenRegistry.() -> Unit
}
