package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class TokenRefreshInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val traktAuthRepository: TraktAuthRepository,
    @IoCoroutineScope private val scope: CoroutineScope,
) {

    public fun init() {
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
