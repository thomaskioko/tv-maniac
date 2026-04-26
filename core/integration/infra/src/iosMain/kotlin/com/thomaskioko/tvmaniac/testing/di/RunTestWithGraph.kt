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
 * iOS variant of [runTestWithGraph]. See the JVM declaration in `jvmMain` for
 * the contract and motivation; the two share their shape but live in separate
 * source sets because Metro materializes `@DependencyGraph.Factory` per target.
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
