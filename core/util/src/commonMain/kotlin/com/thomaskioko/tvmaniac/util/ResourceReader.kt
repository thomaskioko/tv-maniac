package com.thomaskioko.tvmaniac.util

import kotlinx.serialization.DeserializationStrategy
import me.tatarka.inject.annotations.Inject
import net.mamoe.yamlkt.Yaml
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

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
