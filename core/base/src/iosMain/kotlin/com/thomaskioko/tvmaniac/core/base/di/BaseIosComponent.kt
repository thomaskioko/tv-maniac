package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.base.scope.NsQueueCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

@ContributesTo(AppScope::class)
interface BaseIosComponent {

    @Provides
    fun provideAppCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope {
        return AppCoroutineScope(
            default = CoroutineScope(Job() + dispatchers.computation),
            io = CoroutineScope(Job() + dispatchers.io),
            main = NsQueueCoroutineScope(),
        )
    }
}
