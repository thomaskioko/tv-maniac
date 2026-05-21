package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Base class for flags backed by Firebase Remote Config. [observe] delegates to
 * [FeatureFlagsRemoteConfig.observeBoolean] and [observeSource] delegates to
 * [FeatureFlagsRemoteConfig.observeSource]. Local debug-build overrides are layered on by the
 * `DebugRemoteConfig` decorator in the implementation module; this class itself stays free of
 * `DebugConfig` and `FeatureFlagLocalStore` dependencies.
 *
 * Concrete subclasses declare metadata once and inject the shared [FeatureFlagsRemoteConfig]:
 *
 * ```kotlin
 * @SingleIn(AppScope::class)
 * @ContributesIntoSet(AppScope::class, binding = binding<FeatureFlag>())
 * class SimklLoginFlag(remote: FeatureFlagsRemoteConfig) : RemoteFlag(
 *   key = "simkl_login_enabled",
 *   title = "Simkl Login",
 *   description = "Show the Simkl login entry point on the settings screen.",
 *   dateAdded = LocalDate(2026, 5, 17),
 *   defaultValue = false,
 *   remote = remote,
 * )
 * ```
 *
 * @param key Firebase Remote Config key.
 * @param title Human-readable name shown on the debug screen row.
 * @param description One-line summary shown beneath the title on the debug screen.
 * @param dateAdded Date the flag entered the codebase.
 * @param defaultValue Fallback returned until Firebase serves an explicit value.
 * @param remote Shared Firebase Remote Config provider injected via Metro.
 */
public abstract class RemoteFlag(
    override val key: String,
    override val title: String,
    override val description: String,
    override val dateAdded: LocalDate,
    public val defaultValue: Boolean,
    private val remote: FeatureFlagsRemoteConfig,
) : FeatureFlag {

    override fun observe(): Flow<Boolean> = remote.observeBoolean(key, defaultValue)

    override fun observeSource(): Flow<FeatureFlagSource> = remote.observeSource(key)
}
