package com.thomaskioko.tvmaniac.util

import java.io.InputStreamReader
import me.tatarka.inject.annotations.Inject

@Inject
class ClasspathResourceReader : ResourceReader {
  override fun readResource(name: String): String {
    return javaClass.classLoader?.getResourceAsStream(name).use { stream ->
      InputStreamReader(stream).use { reader -> reader.readText() }
    }
  }
}
