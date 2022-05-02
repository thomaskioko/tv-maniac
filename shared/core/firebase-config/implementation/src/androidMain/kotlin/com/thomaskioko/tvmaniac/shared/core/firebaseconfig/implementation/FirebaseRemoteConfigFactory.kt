package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

actual class FirebaseRemoteConfigFactory {

    actual fun build(): PlatformFirebase = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        )
    }
}
