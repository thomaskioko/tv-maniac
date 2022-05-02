package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api

expect interface FeatureFlagsRemoteConfig {
    /**
     * Fetch latest configuration
     */
    suspend fun fetch()

    /**
     * Get value of [flagName] param or flag as [Boolean]
     */
    suspend fun getBoolean(flagName: String): Boolean
}
