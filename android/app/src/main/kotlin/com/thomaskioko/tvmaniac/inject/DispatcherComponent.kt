package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Provides

interface DispatcherComponent {

    @ApplicationScope
    @Provides
    fun provideCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main,
    )
}