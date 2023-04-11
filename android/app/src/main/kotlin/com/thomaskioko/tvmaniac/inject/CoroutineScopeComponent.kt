package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides

interface CoroutineScopeComponent {

    @ApplicationScope
    @Provides
    fun provideCoroutineScope(
        dispatchers: AppCoroutineDispatchers
    ): AppCoroutineScope = AppCoroutineScope(
        default = CoroutineScope(Job() + dispatchers.computation),
        io = CoroutineScope(Job() + dispatchers.io),
        main = CoroutineScope(Job() + dispatchers.main),
    )
}