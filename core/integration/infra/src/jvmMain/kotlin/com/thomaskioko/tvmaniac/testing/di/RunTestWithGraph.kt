package com.thomaskioko.tvmaniac.testing.di

import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Runs [testBody] inside [runTest] with a fresh [TestGraph] and a test
 * dispatcher installed as `Dispatchers.Main`.
 *
 * Collapses the per-test boilerplate of manually calling `Dispatchers.setMain`,
 * `createGraphFactory<TestGraph.Factory>().create()`, and the matching
 * `resetMain` in a teardown. The resulting test body reads:
 *
 * ```kotlin
 * @Test
 * fun `should provide fake DatastoreRepository`() = runTestWithGraph { graph ->
 *     graph.datastoreRepository.shouldBeInstanceOf<FakeDatastoreRepository>()
 * }
 * ```
 *
 * [testDispatcher] defaults to [StandardTestDispatcher] so that any `delay` /
 * `.debounce` inside presenters under test is explicitly driven by
 * `advanceTimeBy(...)` or `advanceUntilIdle()` — rather than eagerly executed
 * and masking ordering bugs.
 *
 * Per-platform declaration: Metro's `@DependencyGraph.Factory` is materialized
 * per Kotlin target, so the JVM and iOS variants of this helper must live in
 * their respective source sets.
 */
public fun runTestWithGraph(
    testDispatcher: TestDispatcher = StandardTestDispatcher(),
    testBody: suspend TestScope.(TestGraph) -> Unit,
): TestResult = runTest(testDispatcher) {
    mainDispatcher(testDispatcher).use {
        val graph = createGraphFactory<TestGraph.Factory>().create()
        graph.drainingScopes(testScheduler).use { testBody(graph) }
    }
}

/** Installs [dispatcher] as `Dispatchers.Main`, restoring the previous main dispatcher on close. */
private fun mainDispatcher(dispatcher: TestDispatcher): AutoCloseable {
    Dispatchers.setMain(dispatcher)
    return AutoCloseable { Dispatchers.resetMain() }
}

/**
 * Cancels the graph's app-lifetime scopes and drains [scheduler] on close, so an eagerly-shared
 * StateFlow (e.g. DefaultSubscriptionManager) stops collecting on `Dispatchers.Main` before it is
 * reset. The JVM [StandardTestDispatcher] queues that eager launch and only drains it after the
 * test body, so without this it dispatches once Main is already reset and throws. Closing this
 * before [mainDispatcher] (nested `use`) drains while Main is still installed. The iOS harness
 * omits it: its immediate-dispatch main runs the eager launch inline during construction.
 */
private fun TestGraph.drainingScopes(scheduler: TestCoroutineScheduler): AutoCloseable =
    AutoCloseable {
        ioCoroutineScope.cancel()
        mainCoroutineScope.cancel()
        scheduler.advanceUntilIdle()
    }
