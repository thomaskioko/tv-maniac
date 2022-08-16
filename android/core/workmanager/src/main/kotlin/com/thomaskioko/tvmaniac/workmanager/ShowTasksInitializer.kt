package com.thomaskioko.tvmaniac.workmanager

import dagger.Lazy
import javax.inject.Inject

class ShowTasksInitializer @Inject constructor(
    private val showTasks: Lazy<ShowTasks>
) : AppInitializer {
    override fun init() {
        showTasks.get().setupDiscoverDailySyncs()
    }
}
