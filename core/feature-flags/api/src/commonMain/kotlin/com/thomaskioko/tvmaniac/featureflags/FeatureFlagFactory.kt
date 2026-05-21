package com.thomaskioko.tvmaniac.featureflags

import kotlinx.datetime.LocalDate

/**
 * Builds [FeatureFlag] instances at construction time. Concrete bindings call factory methods
 * inside `@Provides` functions to assemble each flag's metadata and connect it to the underlying
 * remote-config provider.
 *
 * Single method today; additional methods (`enum`, `integer`, `string`) land alongside the first
 * flag that needs them.
 */
public interface FeatureFlagFactory {

    /**
     * Builds a `Boolean`-typed flag backed by [FeatureFlagsRemoteConfig].
     *
     * @param key Firebase Remote Config key. Drives both Firebase lookups and debug-store overrides.
     * @param title Human-readable name shown on the debug screen row.
     * @param description One-line summary shown beneath the title on the debug screen.
     * @param defaultValue Fallback returned until Firebase serves an explicit value.
     * @param dateAdded Date the flag entered the codebase. Drives the debug screen's "Date Added" sort.
     */
    public fun boolean(
        key: String,
        title: String,
        description: String,
        defaultValue: Boolean,
        dateAdded: LocalDate,
    ): FeatureFlag<Boolean>
}
