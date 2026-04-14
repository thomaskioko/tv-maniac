package com.thomaskioko.tvmaniac.settings.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(ActivityScope::class)
public interface SettingsNavDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideSettingsNavDestination(
            graphFactory: SettingsScreenGraph.Factory,
        ): NavDestination = object : NavDestination {
            override fun matches(config: RootDestinationConfig): Boolean =
                config is RootDestinationConfig.Settings

            override fun createChild(
                config: RootDestinationConfig,
                componentContext: ComponentContext,
            ): RootChild = ScreenDestination(
                presenter = graphFactory.createSettingsGraph(componentContext).settingsPresenter,
            )
        }
    }
}
