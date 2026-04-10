package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class TokenRefreshInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val traktAuthRepository: TraktAuthRepository,
    dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    override fun init() {
        scope.launch {
            traktAuthRepository.state.distinctUntilChanged().collectLatest { state ->
                when (state) {
                    TraktAuthState.LOGGED_IN -> scheduler.schedulePeriodic(TokenRefreshWorker.REQUEST)
                    TraktAuthState.LOGGED_OUT -> scheduler.cancel(TokenRefreshWorker.WORKER_NAME)
                }
            }
        }
    }
}
