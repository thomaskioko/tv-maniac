package com.thomaskioko.tvmaniac.inject

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.app.TvManicApplication
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesGraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.asContribution

@ContributesGraphExtension(ActivityScope::class)
interface ActivityGraph {
    val traktAuthManager: TraktAuthManager
    val rootPresenter: RootPresenter

    @Provides
    fun provideComponentContext(
        activity: ComponentActivity,
    ): ComponentContext = activity.defaultComponentContext()

    @ContributesGraphExtension.Factory(AppScope::class)
    interface Factory {
        fun createComponent(
            @Provides activity: ComponentActivity,
        ): ActivityGraph
    }

    companion object Companion {
        fun create(activity: ComponentActivity): ActivityGraph =
            (activity.application as TvManicApplication)
                .getApplicationComponent()
                .asContribution<Factory>()
                .createComponent(activity)
    }
}
