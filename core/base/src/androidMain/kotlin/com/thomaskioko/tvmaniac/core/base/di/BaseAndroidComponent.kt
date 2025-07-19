package com.thomaskioko.tvmaniac.core.base.di

import android.app.Application
import android.content.Context
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

@ContributesTo(AppScope::class)
interface BaseAndroidComponent {

    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
        AppCoroutineScope(
            default = CoroutineScope(Job() + dispatchers.computation),
            io = CoroutineScope(Job() + dispatchers.io),
            main = CoroutineScope(Job() + dispatchers.main),
        )
}
