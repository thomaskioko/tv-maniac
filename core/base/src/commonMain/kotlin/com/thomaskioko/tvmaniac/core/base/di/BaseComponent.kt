package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface BaseComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        databaseWrite = Dispatchers.IO.limitedParallelism(1),
        databaseRead = Dispatchers.IO.limitedParallelism(4),
        main = Dispatchers.Main,
    )

    @Provides
    @SingleIn(AppScope::class)
    public fun provideAppCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
        AppCoroutineScope(
            default = CoroutineScope(SupervisorJob() + dispatchers.main),
            io = CoroutineScope(SupervisorJob() + dispatchers.io),
            main = CoroutineScope(SupervisorJob() + dispatchers.main),
        )

    @Provides
    public fun provideCoroutineScope(appCoroutineScope: AppCoroutineScope): CoroutineScope =
        appCoroutineScope.main
}
