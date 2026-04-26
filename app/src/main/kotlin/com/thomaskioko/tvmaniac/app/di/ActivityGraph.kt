package com.thomaskioko.tvmaniac.app.di

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.app.TvManicApplication
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.SheetChildFactory
import com.thomaskioko.tvmaniac.navigation.SheetConfigBinding
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.navigation.ui.SheetContent
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.asContribution

@GraphExtension(ActivityScope::class)
@SingleIn(ActivityScope::class)
public interface ActivityGraph {
    public val traktAuthManager: TraktAuthManager
    public val rootPresenter: RootPresenter
    public val navigator: Navigator
    public val screenContents: Set<ScreenContent>
    public val sheetContents: Set<SheetContent>
    public val navDestinations: Set<NavDestination>
    public val navRouteBindings: Set<NavRouteBinding<*>>
    public val sheetChildFactories: Set<SheetChildFactory>
    public val sheetConfigBindings: Set<SheetConfigBinding<*>>

    @Provides
    public fun provideComponentContext(
        activity: ComponentActivity,
    ): ComponentContext = activity.defaultComponentContext()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createGraph(
            @Provides activity: ComponentActivity,
        ): ActivityGraph
    }

    public companion object {
        public fun create(activity: ComponentActivity): ActivityGraph =
            (activity.application as TvManicApplication)
                .getApplicationGraph()
                .asContribution<Factory>()
                .createGraph(activity)
    }
}
