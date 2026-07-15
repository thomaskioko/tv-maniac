package com.thomaskioko.tvmaniac.core.base.extensions

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AsValueTest {

    private val testDispatcher = StandardTestDispatcher()
    private val owner = TestLifecycleOwner()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should mirror emissions given lifecycle is resumed`() = runTest(testDispatcher) {
        owner.registry.resume()
        val stateFlow = MutableStateFlow(1)

        val value = stateFlow.asValue(owner.coroutineScope())
        testScheduler.runCurrent()

        stateFlow.value = 2
        testScheduler.runCurrent()

        value.value shouldBe 2
    }

    @Test
    fun `should pause collection and release subscription given lifecycle stops`() = runTest(testDispatcher) {
        owner.registry.resume()
        val stateFlow = MutableStateFlow(1)
        val value = stateFlow.asValue(owner.coroutineScope())
        testScheduler.runCurrent()

        owner.registry.stop()
        testScheduler.runCurrent()

        stateFlow.subscriptionCount.value shouldBe 0

        stateFlow.value = 2
        testScheduler.runCurrent()

        value.value shouldBe 1
    }

    @Test
    fun `should resubscribe with current value given lifecycle restarts`() = runTest(testDispatcher) {
        owner.registry.resume()
        val stateFlow = MutableStateFlow(1)
        val value = stateFlow.asValue(owner.coroutineScope())
        testScheduler.runCurrent()

        owner.registry.stop()
        testScheduler.runCurrent()
        stateFlow.value = 5

        owner.registry.resume()
        testScheduler.runCurrent()

        stateFlow.subscriptionCount.value shouldBe 1
        value.value shouldBe 5
    }

    @Test
    fun `should defer collection given lifecycle only created`() = runTest(testDispatcher) {
        owner.registry.create()
        val stateFlow = MutableStateFlow(1)

        val value = stateFlow.asValue(owner.coroutineScope())
        testScheduler.runCurrent()

        stateFlow.subscriptionCount.value shouldBe 0

        stateFlow.value = 3
        testScheduler.runCurrent()
        value.value shouldBe 1

        owner.registry.resume()
        testScheduler.runCurrent()
        value.value shouldBe 3
    }

    @Test
    fun `should collect for scope lifetime given plain scope`() = runTest(testDispatcher) {
        val scope = CoroutineScope(SupervisorJob() + testDispatcher)
        val stateFlow = MutableStateFlow(1)

        val value = stateFlow.asValue(scope)
        testScheduler.runCurrent()

        stateFlow.value = 2
        testScheduler.runCurrent()

        value.value shouldBe 2
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    val registry: LifecycleRegistry = LifecycleRegistry()
    override val lifecycle: Lifecycle get() = registry
}
