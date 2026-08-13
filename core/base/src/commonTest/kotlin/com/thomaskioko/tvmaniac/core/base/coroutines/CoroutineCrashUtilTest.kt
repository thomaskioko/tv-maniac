package com.thomaskioko.tvmaniac.core.base.coroutines

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoroutineCrashUtilTest {

    private val testDispatcher = StandardTestDispatcher()
    private val owner = TestLifecycleOwner()
    private val recorded = mutableListOf<Throwable>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        CoroutineCrashUtil.setUncaughtException { recorded += it }
    }

    @AfterTest
    fun tearDown() {
        CoroutineCrashUtil.setUncaughtException { it.printStackTrace() }
        Dispatchers.resetMain()
    }

    @Test
    fun `should forward failure to callback given coroutine in lifecycle scope throws`() = runTest(testDispatcher) {
        owner.registry.resume()
        val cause = IllegalStateException("boom")

        owner.coroutineScope().launch { throw cause }
        testScheduler.runCurrent()

        recorded shouldBe listOf(cause)
    }

    @Test
    fun `should keep sibling coroutines alive given one coroutine fails`() = runTest(testDispatcher) {
        owner.registry.resume()
        val scope = owner.coroutineScope()
        var siblingRan = false

        scope.launch { throw IllegalStateException("boom") }
        scope.launch { siblingRan = true }
        testScheduler.runCurrent()

        siblingRan shouldBe true
        scope.isActive shouldBe true
    }

    @Test
    fun `should retain last value and not restart sharing given upstream flow fails`() = runTest(testDispatcher) {
        owner.registry.resume()
        val scope = owner.coroutineScope()
        val state = flow {
            emit(1)
            throw IllegalStateException("boom")
        }.stateIn(scope, SharingStarted.WhileSubscribed(), 0)

        val collector = launch { state.collect {} }
        testScheduler.runCurrent()

        state.value shouldBe 1
        recorded shouldHaveSize 1

        collector.cancel()
        val resubscribed = launch { state.collect {} }
        testScheduler.runCurrent()

        state.value shouldBe 1
        recorded shouldHaveSize 1
        resubscribed.cancel()
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    val registry: LifecycleRegistry = LifecycleRegistry()
    override val lifecycle: Lifecycle get() = registry
}
