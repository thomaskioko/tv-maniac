package com.thomaskioko.tvmaniac.iosframework

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.appconfig.AppMetadata
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.base.AppInitializers
import com.thomaskioko.tvmaniac.core.base.IsDebugBuild
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.featureflags.RemoteConfigBridge
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
public interface IosApplicationGraph {
    public val initializers: AppInitializers
    public val viewPresenterGraphFactory: IosViewPresenterGraph.Factory
    public val accountAuthRepositories: Map<AccountProvider, AccountAuthRepository>
    public val authClientConfigs: Map<AccountProvider, AuthClientConfig>
    public val backgroundTaskScheduler: BackgroundTaskScheduler
    public val appMetadata: AppMetadata
    public val debugConfig: DebugConfig
    public val logger: Logger

    @DependencyGraph.Factory
    public fun interface Factory {
        public fun create(
            @Provides @IsDebugBuild isDebug: Boolean,
            @Provides remoteConfigBridge: RemoteConfigBridge,
        ): IosApplicationGraph
    }
}
