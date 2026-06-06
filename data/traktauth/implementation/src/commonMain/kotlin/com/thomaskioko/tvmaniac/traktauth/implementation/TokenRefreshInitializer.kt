package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class TokenRefreshInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val connectedAccountRepository: ConnectedAccountRepository,
    @IoCoroutineScope private val scope: CoroutineScope,
) {

    public fun init() {
        scope.launch {
            connectedAccountRepository.activeProvider
                .distinctUntilChanged()
                .collectLatest { provider ->
                    if (provider == ConnectedProvider.TRAKT) {
                        scheduler.scheduleAndExecute(TokenRefreshWorker.REQUEST)
                    } else {
                        scheduler.cancel(TokenRefreshWorker.WORKER_NAME)
                    }
                }
        }
    }
}
