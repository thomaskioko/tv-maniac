package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Type-safe handle to a feature flag. Consumers inject `@QualifierAnnotation FeatureFlag<T>`,
 * where the qualifier annotation identifies which flag and `T` is the value type (today only
 * `Boolean`). Bindings live in the implementation module's `FlagBindings` interface; the debug
 * screen iterates the same flags through the `Set<FeatureFlag<Boolean>>` multibinding contributed
 * alongside each qualified binding.
 *
 * @property key Firebase Remote Config key. Drives both Firebase lookups and debug-store overrides.
 * @property title Human-readable name. Shown on the debug screen row.
 * @property description One-line summary. Shown beneath the title on the debug screen.
 * @property dateAdded Date the flag entered the codebase. Drives the debug screen's "Date Added" sort.
 */
public interface FeatureFlag<T> {
    public val key: String
    public val title: String
    public val description: String
    public val dateAdded: LocalDate

    /**
     * Observes the flag's current value. Emits updates from the Firebase realtime listener and,
     * in debug builds, from local overrides.
     */
    public fun observe(): Flow<T>

    /**
     * Observes the source of the current value. Emits [FeatureFlagSource.Local] when a debug
     * override is active, otherwise [FeatureFlagSource.Firebase].
     */
    public fun observeSource(): Flow<FeatureFlagSource>
}
