package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation

import co.touchlab.kermit.Logger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api.FeatureFlagsRemoteConfig
import kotlinx.coroutines.tasks.await

actual class FirebaseRemoteFeatureFlagsConfig constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureFlagsRemoteConfig {

    override suspend fun fetch() {
        try {
            remoteConfig.fetchAndActivate().await()
            Logger.withTag("@fetch").d { "Firebase remote config fetch successful" }
        } catch (e: Exception) {
            Logger.withTag("@fetch").e("Failed to fetch Firebase remote config")
        }
    }

    override suspend fun getBoolean(flagName: String): Boolean = remoteConfig.getBoolean(flagName)
}
