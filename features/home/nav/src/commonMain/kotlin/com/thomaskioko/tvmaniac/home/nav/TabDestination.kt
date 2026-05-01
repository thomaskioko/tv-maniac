package com.thomaskioko.tvmaniac.home.nav

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.navigation.NavRoot

public interface TabDestination {
    public fun matches(root: NavRoot): Boolean
    public fun createChild(root: NavRoot, componentContext: ComponentContext): TabChild<*>
}
