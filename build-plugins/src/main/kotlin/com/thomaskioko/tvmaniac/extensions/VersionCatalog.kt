package com.thomaskioko.tvmaniac.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.getVersion(name: String): String {
  return getVersionOrNull(name) ?: throw NoSuchElementException("Could not find version $name")
}

internal fun Project.getVersionOrNull(name: String): String? {
  return libs.findVersion(name).orElseGet { null }?.requiredVersion
}
