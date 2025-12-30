package com.thomaskioko.tvmaniac.inject

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.AppInitializers
import com.thomaskioko.tvmaniac.traktauth.implementation.task.TraktAuthWorkerFactory
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
public abstract class ApplicationComponent(
    @get:Provides public val application: Application,
) : ActivityComponent.Factory {
    public abstract val initializers: AppInitializers
    public abstract val activityComponentFactory: ActivityComponent.Factory
    public abstract val workerFactory: TraktAuthWorkerFactory

    public companion object
}
