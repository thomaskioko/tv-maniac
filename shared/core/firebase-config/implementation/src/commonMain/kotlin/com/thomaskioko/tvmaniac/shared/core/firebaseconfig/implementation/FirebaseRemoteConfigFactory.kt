package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation

expect class FirebaseRemoteConfigFactory {
    fun build(): PlatformFirebase
}
