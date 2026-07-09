package com.thomaskioko.tvmaniac.core.base.coroutines

import com.thomaskioko.tvmaniac.core.base.SyncScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@Inject
@SingleIn(AppScope::class)
public class SyncCoroutineScope(
    dispatchers: AppCoroutineDispatchers,
) {
    private val job = SupervisorJob()

    public val scope: CoroutineScope = CoroutineScope(job + dispatchers.io)

    public suspend fun cancelActiveWork() {
        job.cancelChildren()
        withTimeoutOrNull(CANCEL_JOIN_TIMEOUT) {
            job.children.toList().joinAll()
        }
    }

    private companion object {
        private val CANCEL_JOIN_TIMEOUT = 2.seconds
    }
}

@BindingContainer
@ContributesTo(AppScope::class)
public object SyncScopeBindingContainer {

    @Provides
    @SyncScope
    public fun provideSyncScope(syncCoroutineScope: SyncCoroutineScope): CoroutineScope =
        syncCoroutineScope.scope
}
