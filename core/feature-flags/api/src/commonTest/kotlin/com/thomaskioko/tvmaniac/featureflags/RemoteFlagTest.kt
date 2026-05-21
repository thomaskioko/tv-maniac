package com.thomaskioko.tvmaniac.featureflags

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class RemoteFlagTest {

    @Test
    fun `should pass observe through to remote config`() = runTest {
        val booleanFlow = MutableStateFlow(false)
        val flag = TestFlag(MutableTestRemoteConfig(booleanFlow = booleanFlow))

        flag.observe().test {
            awaitItem() shouldBe false
            booleanFlow.value = true
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should pass observeSource through to remote config`() = runTest {
        val sourceFlow = MutableStateFlow(FeatureFlagSource.Firebase)
        val flag = TestFlag(MutableTestRemoteConfig(sourceFlow = sourceFlow))

        flag.observeSource().test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            sourceFlow.value = FeatureFlagSource.Local
            awaitItem() shouldBe FeatureFlagSource.Local
        }
    }
}

private class TestFlag(remote: FeatureFlagsRemoteConfig) : RemoteFlag(
    key = "test_flag",
    title = "Test",
    description = "Test description.",
    dateAdded = LocalDate(2026, 1, 1),
    defaultValue = false,
    remote = remote,
)

private class MutableTestRemoteConfig(
    private val booleanFlow: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val sourceFlow: MutableStateFlow<FeatureFlagSource> = MutableStateFlow(FeatureFlagSource.Firebase),
) : FeatureFlagsRemoteConfig {
    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> = booleanFlow
    override fun observeSource(key: String): Flow<FeatureFlagSource> = sourceFlow
    override suspend fun refresh() = Unit
    override suspend fun setDefaults(defaults: Map<String, Boolean>) = Unit
}
