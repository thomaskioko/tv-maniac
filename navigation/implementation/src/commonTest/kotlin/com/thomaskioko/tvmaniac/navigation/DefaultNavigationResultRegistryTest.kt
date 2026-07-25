package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test

internal class DefaultNavigationResultRegistryTest {

    private data object OwnerRoute : NavRoute

    @Serializable
    private data class TestResult(val value: String)

    private val key = NavigationResultRequest.Key<TestResult>(
        ownerRouteQualifiedName = OwnerRoute::class.qualifiedName!!,
        resultQualifiedName = TestResult::class.qualifiedName!!,
    )

    @Test
    fun `should deliver result given collector registered first`() = runTest {
        val registry = DefaultNavigationResultRegistry()

        registry.register(key).test {
            registry.deliver(key, TestResult("delivered"))
            awaitItem() shouldBe TestResult("delivered")
        }
    }

    @Test
    fun `should buffer result given delivered before registration`() = runTest {
        val registry = DefaultNavigationResultRegistry()

        registry.deliver(key, TestResult("buffered"))

        registry.register(key).test {
            awaitItem() shouldBe TestResult("buffered")
        }
    }

    @Test
    fun `should keep latest result given multiple deliveries before collection`() = runTest {
        val registry = DefaultNavigationResultRegistry()

        registry.deliver(key, TestResult("first"))
        registry.deliver(key, TestResult("second"))

        registry.register(key).test {
            awaitItem() shouldBe TestResult("second")
        }
    }

    @Test
    fun `should deliver to single collector given per-key semantics`() = runTest {
        val registry = DefaultNavigationResultRegistry()

        registry.register(key).test {
            registry.deliver(key, TestResult("once"))
            awaitItem() shouldBe TestResult("once")
            expectNoEvents()
        }
    }

    @Test
    fun `should treat keys with equal fields as equal`() {
        val other = NavigationResultRequest.Key<TestResult>(
            ownerRouteQualifiedName = OwnerRoute::class.qualifiedName!!,
            resultQualifiedName = TestResult::class.qualifiedName!!,
        )

        other shouldBe key
    }
}
