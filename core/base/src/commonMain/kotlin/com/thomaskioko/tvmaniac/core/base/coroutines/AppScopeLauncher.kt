package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Launches work that must outlive the caller's coroutine scope.
 *
 * Use this for follow-up sync work triggered by a user action (writing through to the
 * network, refreshing caches) that should still complete if the user navigates away
 * before the launched block finishes. The block runs on a process-scoped supervisor,
 * so cancelling the caller does not cancel the launched job.
 *
 * Process-scoped, not OS-scheduled. The job dies with the process. For work that must
 * survive process death, use the OS-managed scheduler in `core/tasks` instead.
 *
 * The launcher catches any failure inside the block and logs it under the supplied
 * tag. CancellationException is rethrown (never logged), so structured cancellation
 * during process shutdown still works.
 */
public interface AppScopeLauncher {

    public fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job
}
