package com.thomaskioko.tvmaniac.testing.di

import dev.zacsweers.metro.createGraphFactory
import kotlin.reflect.KClass

actual inline fun <reified T : Any> KClass<T>.create(): T {
    return when (this) {
        TestJvmComponent::class -> {
            val factory = createGraphFactory<TestJvmComponent.Factory>()
            factory.create() as T
        }
        else -> throw IllegalArgumentException("Unknown component type: $this")
    }
}
