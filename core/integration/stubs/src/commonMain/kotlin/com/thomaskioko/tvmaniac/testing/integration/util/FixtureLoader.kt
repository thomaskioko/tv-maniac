package com.thomaskioko.tvmaniac.testing.integration.util

public object FixtureLoader {

    public fun load(path: String): String = readFixture("fixtures/$path")
}

internal expect fun readFixture(resourcePath: String): String
