package com.thomaskioko.tvmaniac.core.tasks.implementation.di

import android.app.Application
import androidx.work.WorkManager
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface WorkManagerComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.Companion.getInstance(application)
    }
}
