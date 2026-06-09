package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
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
    private val accountManager: AccountManager,
    @IoCoroutineScope private val scope: CoroutineScope,
) {

    public fun init() {
        scope.launch {
            accountManager.activeProvider
                .distinctUntilChanged()
                .collectLatest { provider ->
                    if (provider == AccountProvider.TRAKT) {
                        scheduler.scheduleAndExecute(TokenRefreshWorker.REQUEST)
                    } else {
                        scheduler.cancel(TokenRefreshWorker.WORKER_NAME)
                    }
                }
        }
    }
}
