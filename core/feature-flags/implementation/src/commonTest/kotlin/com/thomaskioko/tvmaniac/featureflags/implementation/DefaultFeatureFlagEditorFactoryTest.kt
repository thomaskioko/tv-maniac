package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultFeatureFlagEditorFactoryTest {

    private val targetFlag = FeatureFlag.SIMKL_LOGIN_ENABLED
    private val factory = DefaultFeatureFlagEditorFactory()

    @Test
    fun `should mint editor that forwards set to onSet callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, Int>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = factory.create<Int>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set(7)

        setCaptured shouldBe listOf(targetFlag to 7)
        clearCaptured.shouldBeEmpty()
    }

    @Test
    fun `should mint editor that forwards null set to onClear callback`() = runTest {
        val setCaptured = mutableListOf<Pair<FeatureFlag, String>>()
        val clearCaptured = mutableListOf<FeatureFlag>()
        val editor = factory.create<String>(
            flag = targetFlag,
            onSet = { flag, value -> setCaptured += flag to value },
            onClear = { flag -> clearCaptured += flag },
        )

        editor.set(null)

        setCaptured.shouldBeEmpty()
        clearCaptured shouldBe listOf(targetFlag)
    }
}
