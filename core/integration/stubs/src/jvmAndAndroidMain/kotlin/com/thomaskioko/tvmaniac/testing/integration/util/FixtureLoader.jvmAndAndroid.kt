package com.thomaskioko.tvmaniac.testing.integration.util

internal actual fun readFixture(resourcePath: String): String {
    val classLoader = checkNotNull(FixtureLoader::class.java.classLoader) {
        "ClassLoader is null — cannot load fixture: $resourcePath"
    }
    val stream = checkNotNull(classLoader.getResourceAsStream(resourcePath)) {
        "Fixture not found on classpath: $resourcePath"
    }
    return stream.bufferedReader().use { it.readText() }
}
