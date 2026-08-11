package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.Initializers
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.coroutines.CoroutineCrashUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val LOG_TAG = "UncaughtCoroutineException"

@Inject
public class LoggingInitializer(
    private val debugConfig: DebugConfig,
    private val crashReporter: CrashReporter,
    private val crashReportingPreference: CrashReportingPreference,
    private val logger: Logger,
    @IoCoroutineScope private val scope: CoroutineScope,
) {

    public fun init() {
        logger.setup(debugConfig.isDebug)

        CoroutineCrashUtil.setUncaughtException { throwable ->
            logger.error(LOG_TAG, "Uncaught coroutine exception", throwable)
        }

        scope.launch {
            crashReportingPreference.observeCrashReportingEnabled()
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
