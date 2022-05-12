package com.thomaskioko.tvmaniac.injection

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation.FirebaseRemoteConfigFactory
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation.FirebaseRemoteFeatureFlagsConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseFeatureFlagsConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfigFactory().build()

    @Singleton
    @Provides
    fun provideFirebaseFeatureFlagsRemoteConfig(
        remoteConfig: FirebaseRemoteConfig
    ): FeatureFlagsRemoteConfig = FirebaseRemoteFeatureFlagsConfig(remoteConfig)
}
