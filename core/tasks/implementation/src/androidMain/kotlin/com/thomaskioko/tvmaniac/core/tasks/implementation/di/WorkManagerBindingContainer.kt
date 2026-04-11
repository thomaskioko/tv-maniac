package com.thomaskioko.tvmaniac.core.tasks.implementation.di

import android.app.Application
import androidx.work.WorkManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
public object WorkManagerBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.Companion.getInstance(application)
    }
}
