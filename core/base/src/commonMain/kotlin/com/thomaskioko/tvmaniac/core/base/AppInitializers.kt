package com.thomaskioko.tvmaniac.core.base

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
public class AppInitializers(
    @Initializers private val initializers: Set<Initializer>,
    @AsyncInitializers private val asyncInitializers: Set<Initializer>,
    @IoCoroutineScope private val scope: CoroutineScope,
) {
    public fun initialize() {
        for (initializer in initializers) {
            initializer()
        }
        scope.launch {
            for (initializer in asyncInitializers) {
                initializer()
            }
        }
    }
}
