package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Type-safe handle to a feature flag. Each concrete subtype is its own injectable Metro binding;
 * consumers declare the specific flag they depend on (for example,
 * [com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlag]) and read via [observe].
 *
 * Interface is sealed so the debug screen iterates every flag through a `Set<FeatureFlag>`
 * multi-binding without consulting an external registry. Future tiers (`LocalFlag`,
 * `ExperimentFlag`) extend this interface in the same package.
 *
 * @property key Firebase Remote Config key. Drives both Firebase lookups and debug-store overrides.
 * @property title Human-readable name. Shown on the debug screen row.
 * @property description One-line summary. Shown beneath the title on the debug screen.
 * @property dateAdded Date the flag entered the codebase. Drives the debug screen's "Date Added" sort.
 */
public sealed interface FeatureFlag {
    public val key: String
    public val title: String
    public val description: String
    public val dateAdded: LocalDate

    /**
     * Observes the flag's current value. Emits updates from the Firebase realtime listener and,
     * in debug builds, from local overrides.
     */
    public fun observe(): Flow<Boolean>

    /**
     * Observes the source of the current value. Emits [FeatureFlagSource.Local] when a debug
     * override is active, otherwise [FeatureFlagSource.Firebase].
     */
    public fun observeSource(): Flow<FeatureFlagSource>
}
