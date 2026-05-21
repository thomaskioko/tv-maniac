package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow

public interface FeatureFlagsRemoteConfig {

    /**
     * Observes the boolean value for [key]. Emits [default] until Firebase serves an explicit
     * value. Updates the flow as the realtime listener pushes new values.
     */
    public fun observeBoolean(key: String, default: Boolean): Flow<Boolean>

    /**
     * Observes the source of the current value for [key]. Production implementations emit
     * [FeatureFlagSource.Firebase]; the debug decorator emits [FeatureFlagSource.Local] when an
     * override is active.
     */
    public fun observeSource(key: String): Flow<FeatureFlagSource>

    /**
     * Fetches Firebase Remote Config and activates the result. Triggered manually by the debug
     * screen's Force Refresh action. The realtime listener picks up server-side changes
     * automatically without calling this.
     */
    public suspend fun refresh()

    /**
     * Seeds Firebase Remote Config defaults from [defaults]. Called once at application start
     * with the union of every [RemoteFlag]'s `defaultValue`.
     */
    public suspend fun setDefaults(defaults: Map<String, Boolean>)
}
