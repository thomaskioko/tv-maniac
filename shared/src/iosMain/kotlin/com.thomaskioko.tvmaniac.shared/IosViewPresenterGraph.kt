package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigator
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ActivityScope::class)
interface IosViewPresenterGraph {
    val rootPresenter: RootPresenter
    val rootNavigator: RootNavigator

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createComponent(
            @Provides componentContext: ComponentContext,
        ): IosViewPresenterGraph
    }
}
