package com.thomaskioko.tvmaniac.core.tasks.testing

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class FakeBackgroundTaskScheduler : BackgroundTaskScheduler {
    override fun schedulePeriodic(request: PeriodicTaskRequest) {}
    override fun scheduleAndExecute(request: PeriodicTaskRequest) {}
    override fun cancel(id: String) {}
    override fun cancelAll() {}
}
