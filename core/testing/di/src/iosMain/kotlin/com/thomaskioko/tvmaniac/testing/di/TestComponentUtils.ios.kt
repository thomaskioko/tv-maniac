package com.thomaskioko.tvmaniac.testing.di

import dev.zacsweers.metro.createGraphFactory
import kotlin.reflect.KClass

actual inline fun <reified T : Any> KClass<T>.create(): T {
    return when (this) {
        TestIosComponent::class -> {
            val factory = createGraphFactory<TestIosComponent.Factory>()
            factory.create() as T
        }
        else -> throw IllegalArgumentException("Unknown component type: $this")
    }
}
