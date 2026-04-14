package com.thomaskioko.tvmaniac.settings.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.settings.nav.scope.SettingsScreenScope
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(SettingsScreenScope::class)
public interface SettingsScreenGraph {
    public val settingsPresenter: SettingsPresenter

    @ContributesTo(ActivityScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createSettingsGraph(
            @Provides componentContext: ComponentContext,
        ): SettingsScreenGraph
    }
}
