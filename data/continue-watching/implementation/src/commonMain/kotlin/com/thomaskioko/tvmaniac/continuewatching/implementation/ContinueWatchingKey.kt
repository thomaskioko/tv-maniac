package com.thomaskioko.tvmaniac.continuewatching.implementation

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Identifies which continue-watching fetcher backs a Store5 lookup.
 *
 * The key does NOT carry [forceRefresh]: it stays purely an identity for the
 * fetcher dispatch so Store5 maintains one cache slot per fetcher, not per
 * (fetcher, forceRefresh) combination. Force-refresh intent flows through
 * [FetchHints] on the coroutine context instead. See [ContinueWatchingStore]
 * for how the store wires the two together.
 */
public sealed interface ContinueWatchingKey {
    public data object Progress : ContinueWatchingKey
    public data object Nitro : ContinueWatchingKey
}

/**
 * Coroutine context element that carries call-time fetch hints into the Store5
 * fetcher lambda. Set by [ContinueWatchingStore.fetchWith]; read by the
 * fetcher inside [ContinueWatchingStore].
 *
 * Using a context element instead of a mutable class field keeps the hint
 * scoped to a single call: concurrent calls with different [forceRefresh]
 * values do not race on shared state.
 */
internal class FetchHints(
    val forceRefresh: Boolean,
) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<FetchHints>
}

internal class FetcherSkipSignal(message: String) : RuntimeException(message)
