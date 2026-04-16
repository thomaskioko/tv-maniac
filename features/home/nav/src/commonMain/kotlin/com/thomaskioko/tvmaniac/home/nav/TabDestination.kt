package com.thomaskioko.tvmaniac.home.nav

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig

public interface TabDestination {
    public fun matches(config: HomeConfig): Boolean
    public fun createChild(config: HomeConfig, componentContext: ComponentContext): TabChild<*>
}
