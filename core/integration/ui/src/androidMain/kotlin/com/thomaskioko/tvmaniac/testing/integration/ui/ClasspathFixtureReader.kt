package com.thomaskioko.tvmaniac.testing.integration.ui

import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader

public class ClasspathFixtureReader : FixtureReader {
    override fun read(path: String): String {
        return FixtureLoader.load(path)
    }
}
