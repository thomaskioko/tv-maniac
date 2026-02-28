package com.thomaskioko.tvmaniac.core.logger

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class LoggingInitializer(
    private val crashReporter: CrashReporter,
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
    dispatchers: AppCoroutineDispatchers,
) : AppInitializer {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    override fun init() {
        logger.setup(BuildConfig.IS_DEBUG)

        scope.launch {
            datastoreRepository.observeCrashReportingEnabled()
                .collect {
                    crashReporter.setCollectionEnabled(it)
                }
        }
    }
}
