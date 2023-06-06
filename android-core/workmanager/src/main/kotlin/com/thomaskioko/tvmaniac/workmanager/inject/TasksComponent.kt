package com.thomaskioko.tvmaniac.workmanager.inject

import android.app.Application
import androidx.work.WorkManager
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import com.thomaskioko.tvmaniac.workmanager.AppInitializer
import com.thomaskioko.tvmaniac.workmanager.ShowTasks
import com.thomaskioko.tvmaniac.workmanager.ShowTasksImpl
import com.thomaskioko.tvmaniac.workmanager.DiscoverTasksInitializer
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface TasksComponent {

    @ApplicationScope
    @Provides
    fun provideWorkManager(
        application: Application,
    ): WorkManager = WorkManager.getInstance(application)

    @ApplicationScope
    @Provides
    @IntoSet
    fun provideShowTasksInitializer(bind: DiscoverTasksInitializer): AppInitializer = bind

    @ApplicationScope
    @Provides
    fun provideShowTasks(bind: ShowTasksImpl): ShowTasks = bind
}
