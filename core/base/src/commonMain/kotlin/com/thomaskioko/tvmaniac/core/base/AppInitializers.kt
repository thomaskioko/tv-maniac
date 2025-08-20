package com.thomaskioko.tvmaniac.core.base

import com.thomaskioko.tvmaniac.core.base.di.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.di.Initializers
import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
class AppInitializers(
    @Initializers private val syncInitializers: Set<() -> Unit>,
    @AsyncInitializers private val asyncInitializers: Set<() -> Unit>,
    @IoCoroutineScope private val ioScope: CoroutineScope,
) {
    fun initialize() {
        for (initializer in syncInitializers) {
            initializer()
        }

        ioScope.launch {
            for (initializer in asyncInitializers) {
                initializer()
            }
        }
    }
}
