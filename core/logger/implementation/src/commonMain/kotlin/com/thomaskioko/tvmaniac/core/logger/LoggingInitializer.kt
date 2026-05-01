package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.appconfig.ApplicationInfo
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
public class LoggingInitializer(
    private val applicationInfo: ApplicationInfo,
    private val crashReporter: CrashReporter,
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
    @IoCoroutineScope private val scope: CoroutineScope,
) {

    public fun init() {
        logger.setup(applicationInfo.debugBuild)

        scope.launch {
            datastoreRepository.observeCrashReportingEnabled()
                .collect {
                    crashReporter.setCollectionEnabled(it)
                }
        }
    }
}

@ContributesTo(AppScope::class)
public interface LoggingInitializerModule {
    public companion object {
        @Provides
        @IntoSet
        @Initializers
        public fun provideLoggingInitializer(bind: LoggingInitializer): Initializer = Initializer { bind.init() }
    }
}
