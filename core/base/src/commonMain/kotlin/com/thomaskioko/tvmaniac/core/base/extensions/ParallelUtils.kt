package com.thomaskioko.tvmaniac.core.base.extensions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * Executes the given [block] for each element of the [Iterable] in parallel.
 *
 * This function converts the iterable to a [kotlinx.coroutines.flow.Flow] and uses
 * [flatMapMerge] to process items concurrently.
 *
 * @param T The type of elements in the iterable.
 * @param concurrency The maximum number of coroutines that can be executed at the same time.
 * Defaults to [DEFAULT_CONCURRENCY].
 * @param block The suspending function to be executed for each element.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
public suspend fun <T> Iterable<T>.parallelForEach(
    concurrency: Int = DEFAULT_CONCURRENCY,
    block: suspend (value: T) -> Unit,
) {
    asFlow()
        .flatMapMerge(concurrency = concurrency) { item ->
            flow {
                block(item)
                emit(Unit)
            }
        }
        .collect()
}
