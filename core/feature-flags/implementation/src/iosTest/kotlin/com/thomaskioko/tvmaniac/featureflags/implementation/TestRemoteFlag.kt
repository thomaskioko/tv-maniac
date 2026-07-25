package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

internal class TestRemoteFlag(key: String) : RemoteFlag(
    key = key,
    title = "Test",
    description = "Test flag.",
    dateAdded = LocalDate(2026, 1, 1),
    defaultValue = false,
    remote = NoOpRemoteConfig,
)

private object NoOpRemoteConfig : FeatureFlagsRemoteConfig {
    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> = flowOf(default)

    override fun observeSource(key: String): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)

    override suspend fun refresh(): Unit = Unit

    override suspend fun setDefaults(defaults: Map<String, Boolean>): Unit = Unit
}
