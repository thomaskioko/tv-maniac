package com.thomaskioko.tvmaniac.core.tasks.testing

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest

public class FakeBackgroundTaskScheduler : BackgroundTaskScheduler {
    private val scheduledRequests = mutableListOf<PeriodicTaskRequest>()
    private val cancelledIds = mutableListOf<String>()

    override fun schedulePeriodic(request: PeriodicTaskRequest) {
        scheduledRequests += request
    }

    override fun scheduleAndExecute(request: PeriodicTaskRequest) {
        scheduledRequests += request
    }

    override fun cancel(id: String) {
        cancelledIds += id
    }

    override fun cancelAll() {}

    public fun getScheduledRequests(): List<PeriodicTaskRequest> = scheduledRequests.toList()

    public fun getCancelledIds(): List<String> = cancelledIds.toList()
}
