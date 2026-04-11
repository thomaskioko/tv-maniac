package com.thomaskioko.tvmaniac.iosframework

import com.thomaskioko.tvmaniac.core.base.AppInitializers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
public interface IosApplicationGraph {
    public val initializers: AppInitializers
    public val viewPresenterGraphFactory: IosViewPresenterGraph.Factory
    public val traktAuthRepository: TraktAuthRepository
    public val traktAuthManager: TraktAuthManager
    public val backgroundTaskScheduler: BackgroundTaskScheduler
    public val logger: Logger

    public companion object {
        public fun create(): IosApplicationGraph = createGraph<IosApplicationGraph>()
    }
}
