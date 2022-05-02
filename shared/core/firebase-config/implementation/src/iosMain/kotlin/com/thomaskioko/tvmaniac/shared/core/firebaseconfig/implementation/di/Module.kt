package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation.di

import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation.FirebaseRemoteConfigFactory
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation.FirebaseRemoteFeatureFlagsConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun fireBaseConfigPlatformModule(): Module = module {

    single { FirebaseRemoteConfigFactory().build() }
    factory { FirebaseRemoteFeatureFlagsConfig(get()) }
}
