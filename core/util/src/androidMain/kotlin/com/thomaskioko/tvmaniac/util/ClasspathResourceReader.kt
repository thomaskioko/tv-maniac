package com.thomaskioko.tvmaniac.util

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
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
