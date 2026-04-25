package com.thomaskioko.tvmaniac.testing.integration.util

public object FixtureLoader {

    public fun load(path: String): String {
        val resourcePath = "fixtures/$path"
        val classLoader = checkNotNull(FixtureLoader::class.java.classLoader) {
            "ClassLoader is null — cannot load fixture: $resourcePath"
        }
        val stream = checkNotNull(classLoader.getResourceAsStream(resourcePath)) {
            "Fixture not found on classpath: $resourcePath"
        }
        return stream.bufferedReader().use { it.readText() }
    }
}
