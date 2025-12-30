package com.thomaskioko.tvmaniac.inject

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.app.TvManicApplication
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesSubcomponent(ActivityScope::class)
@SingleIn(ActivityScope::class)
public interface ActivityComponent {
    public val traktAuthManager: TraktAuthManager
    public val rootPresenter: RootPresenter

    @Provides
    public fun provideComponentContext(
        activity: ComponentActivity,
    ): ComponentContext = activity.defaultComponentContext()

    @ContributesSubcomponent.Factory(AppScope::class)
    public interface Factory {
        public fun createComponent(
            activity: ComponentActivity,
        ): ActivityComponent
    }

    public companion object {
        public fun create(activity: ComponentActivity): ActivityComponent =
            (activity.application as TvManicApplication)
                .getApplicationComponent()
                .activityComponentFactory
                .createComponent(activity)
    }
}
