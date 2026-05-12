package com.thomaskioko.tvmaniac.app.di

import android.app.Application
import com.thomaskioko.tvmaniac.app.util.TvManiacWorkerFactory
import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.base.AppInitializers
import com.thomaskioko.tvmaniac.core.base.IsDebugBuild
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
public interface ApplicationGraph {
    public val appMetadata: AppMetadata
    public val debugConfig: DebugConfig
    public val initializers: AppInitializers
    public val workerFactory: TvManiacWorkerFactory

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(
            @Provides application: Application,
            @Provides @IsDebugBuild isDebug: Boolean,
        ): ApplicationGraph
    }
}
