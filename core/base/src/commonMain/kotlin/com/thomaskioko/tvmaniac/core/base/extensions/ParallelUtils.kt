package com.thomaskioko.tvmaniac.core.base.extensions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

public const val DEFAULT_SYNC_CONCURRENCY: Int = 6

/**
 * Executes the given [block] for each element of the [Iterable] in parallel.
 *
 * This function converts the iterable to a [kotlinx.coroutines.flow.Flow] and uses
 * [flatMapMerge] to process items concurrently.
 *
 * @param T The type of elements in the iterable.
 * @param concurrency The maximum number of coroutines that can be executed at the same time.
 * Defaults to [DEFAULT_SYNC_CONCURRENCY].
 * @param block The suspending function to be executed for each element.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
public suspend fun <T> Iterable<T>.parallelForEach(
    concurrency: Int = DEFAULT_SYNC_CONCURRENCY,
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

/**
 * Maps each element of the [Iterable] via [block] in parallel and collects the
 * results into a list. Like [parallelForEach] but returns per-element values
 * instead of `Unit`.
 *
 * Result order is NOT preserved: items are emitted as their coroutines finish,
 * so faster-completing work appears earlier in the returned list. Callers that
 * need to associate a result with its input should map each element to a
 * `Pair(input, result)` inside [block].
 *
 * @param T The type of input elements.
 * @param R The type of mapped results.
 * @param concurrency The maximum number of coroutines that can be executed at the same time.
 * Defaults to [DEFAULT_SYNC_CONCURRENCY].
 * @param block The suspending function applied to each element.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
public suspend fun <T, R> Iterable<T>.parallelMap(
    concurrency: Int = DEFAULT_SYNC_CONCURRENCY,
    block: suspend (value: T) -> R,
): List<R> {
    val results = mutableListOf<R>()
    asFlow()
        .flatMapMerge(concurrency = concurrency) { item ->
            flow { emit(block(item)) }
        }
        .collect { results.add(it) }
    return results
}
