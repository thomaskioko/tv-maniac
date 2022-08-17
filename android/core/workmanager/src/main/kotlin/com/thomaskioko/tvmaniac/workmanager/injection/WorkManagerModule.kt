package com.thomaskioko.tvmaniac.workmanager.injection

import android.content.Context
import androidx.work.WorkManager
import com.thomaskioko.tvmaniac.workmanager.AppInitializer
import com.thomaskioko.tvmaniac.workmanager.ShowTasks
import com.thomaskioko.tvmaniac.workmanager.ShowTasksImpl
import com.thomaskioko.tvmaniac.workmanager.ShowTasksInitializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)
}

@InstallIn(SingletonComponent::class)
@Module
interface WorkManagerModuleBinds {

    @Binds
    @IntoSet
    fun provideShowTasksInitializer(bind: ShowTasksInitializer): AppInitializer

    @Binds
    @Singleton
    fun provideShowTasks(bind: ShowTasksImpl): ShowTasks
}
