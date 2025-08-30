package com.thomaskioko.tvmaniac.util

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.io.InputStreamReader

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ClasspathResourceReader : ResourceReader {
    override fun readResource(name: String): String {
        return javaClass.classLoader?.getResourceAsStream(name).use { stream ->
            InputStreamReader(stream).use { reader -> reader.readText() }
        }
    }
}
