package com.thomaskioko.tvmaniac.testing.di

import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
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
    Dispatchers.setMain(testDispatcher)
    try {
        val graph = createGraphFactory<TestGraph.Factory>().create()
        testBody(graph)
    } finally {
        Dispatchers.resetMain()
    }
}
