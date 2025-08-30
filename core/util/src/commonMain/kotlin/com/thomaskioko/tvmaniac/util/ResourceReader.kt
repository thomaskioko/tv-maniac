package com.thomaskioko.tvmaniac.util

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.DeserializationStrategy
import net.mamoe.yamlkt.Yaml

interface ResourceReader {
    fun readResource(name: String): String
}

@Inject
@SingleIn(AppScope::class)
class YamlResourceReader(
    private val resourceReader: ResourceReader,
) {
    internal fun <T> readAndDecodeResource(name: String, strategy: DeserializationStrategy<T>): T {
        val text = resourceReader.readResource(name)
        return Yaml.decodeFromString(strategy, text)
    }
}
