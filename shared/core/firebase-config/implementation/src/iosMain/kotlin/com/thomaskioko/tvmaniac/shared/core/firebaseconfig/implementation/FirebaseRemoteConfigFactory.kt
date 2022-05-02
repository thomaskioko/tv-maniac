package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation

import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings

actual class FirebaseRemoteConfigFactory {

    actual fun build(): PlatformFirebase {
        val iosSettings = FIRRemoteConfigSettings().apply {
            minimumFetchInterval = 5.0
        }

        val remoteConfig = FIRRemoteConfig.remoteConfig()
        remoteConfig.setConfigSettings(iosSettings)
        return remoteConfig
    }
}
