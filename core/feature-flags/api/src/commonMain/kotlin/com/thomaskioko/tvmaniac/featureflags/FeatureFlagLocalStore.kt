package com.thomaskioko.tvmaniac.featureflags

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * Stores debug-build local overrides keyed by Firebase Remote Config key. Persists across process
 * restarts (DataStore-backed). Used only by the `DebugRemoteConfig` decorator; production builds
 * never read or write through this interface.
 *
 * The store is generic over value type so a future non-boolean flag tier can use the same storage
 * without rewiring the API. Today only `Boolean` values are exercised.
 */
public interface FeatureFlagLocalStore {

    /** Observes the local override for [key]. Emits null until [set] is called for the same [key]
     *  and a value matching [type] has been written. */
    public fun <T : Any> observe(key: String, type: KClass<T>): Flow<T?>

    /** Observes the full set of stored override entries. Used by the debug screen to show which
     *  flags currently carry a local override. */
    public fun observeAll(): Flow<Map<String, Any>>

    /** Writes a local override for [key]. Persists across process restarts. */
    public suspend fun <T : Any> set(key: String, value: T)

    /** Removes the local override for [key], reverting reads to the production [FeatureFlagsRemoteConfig]. */
    public suspend fun clear(key: String)

    /** Removes every local override. Triggered by the debug screen's "Reset all" action. */
    public suspend fun clearAll()
}

/** Convenience overload that reifies [T] for callers that read a known value type. */
public inline fun <reified T : Any> FeatureFlagLocalStore.observe(key: String): Flow<T?> =
    observe(key, T::class)
