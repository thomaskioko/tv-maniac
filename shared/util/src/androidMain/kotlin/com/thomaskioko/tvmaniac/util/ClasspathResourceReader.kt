package com.thomaskioko.tvmaniac.util

import me.tatarka.inject.annotations.Inject
import java.io.InputStreamReader

@Inject
class ClasspathResourceReader : ResourceReader {
    override fun readResource(name: String): String {
        return javaClass.classLoader?.getResourceAsStream(name).use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}