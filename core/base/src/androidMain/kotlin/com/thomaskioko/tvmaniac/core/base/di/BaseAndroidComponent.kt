package com.thomaskioko.tvmaniac.core.base.di

import android.app.Application
import android.content.Context
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

@ContributesTo(AppScope::class)
interface BaseAndroidComponent {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    @MainCoroutineScope
    fun provideMainCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(Job() + dispatchers.main)

    @Provides
    @SingleIn(AppScope::class)
    @IoCoroutineScope
    fun provideIoCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(Job() + dispatchers.io)

    @Provides
    @SingleIn(AppScope::class)
    @ComputationCoroutineScope
    fun provideComputationCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope =
        CoroutineScope(Job() + dispatchers.computation)

    @Provides
    fun provideAppCoroutineScope(
        @MainCoroutineScope mainScope: CoroutineScope,
        @IoCoroutineScope ioScope: CoroutineScope,
        @ComputationCoroutineScope computationScope: CoroutineScope,
    ): AppCoroutineScope = AppCoroutineScope(
        default = computationScope,
        io = ioScope,
        main = mainScope,
    )
}
