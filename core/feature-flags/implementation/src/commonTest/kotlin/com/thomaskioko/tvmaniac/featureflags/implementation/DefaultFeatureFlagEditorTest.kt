package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultFeatureFlagEditorTest {

    private val targetFlag = FeatureFlag.SIMKL_LOGIN_ENABLED

    @Test
    fun `should forward set Int value to onSet callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, Int>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = DefaultFeatureFlagEditor<Int>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set(42)

        setCaptured shouldBe listOf(targetFlag to 42)
        clearCaptured.shouldBeEmpty()
    }

    @Test
    fun `should forward null Int set to onClear callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, Int>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = DefaultFeatureFlagEditor<Int>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set(null)

        setCaptured.shouldBeEmpty()
        clearCaptured shouldBe listOf(targetFlag)
    }

    @Test
    fun `should forward set String value to onSet callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, String>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = DefaultFeatureFlagEditor<String>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set("hello")

        setCaptured shouldBe listOf(targetFlag to "hello")
        clearCaptured.shouldBeEmpty()
    }

    @Test
    fun `should forward null String set to onClear callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, String>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = DefaultFeatureFlagEditor<String>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set(null)

        setCaptured.shouldBeEmpty()
        clearCaptured shouldBe listOf(targetFlag)
    }
}
