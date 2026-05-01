package com.thomaskioko.tvmaniac.core.tasks.testing

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest

public class FakeBackgroundTaskScheduler : BackgroundTaskScheduler {
    override fun schedulePeriodic(request: PeriodicTaskRequest) {}
    override fun scheduleAndExecute(request: PeriodicTaskRequest) {}
    override fun cancel(id: String) {}
    override fun cancelAll() {}
}
