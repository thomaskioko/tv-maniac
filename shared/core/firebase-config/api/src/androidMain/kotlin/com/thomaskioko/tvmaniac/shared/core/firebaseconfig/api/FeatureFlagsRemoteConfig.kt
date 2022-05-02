package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api

actual interface FeatureFlagsRemoteConfig {
    /**
     * Fetch latest configuration
     */
    actual suspend fun fetch()

    /**
     * Get value of [flagName] param or flag as [Boolean]
     */
    actual suspend fun getBoolean(flagName: String): Boolean
}
