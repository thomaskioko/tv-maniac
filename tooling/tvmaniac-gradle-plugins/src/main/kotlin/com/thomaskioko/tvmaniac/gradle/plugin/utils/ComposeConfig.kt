package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

/**
 * Apply Compose configuration to the project.
 */
internal fun Project.setupCompose() {
  plugins.apply("org.jetbrains.kotlin.plugin.compose")

  val enableMetrics = project.booleanProperty("compose.enableCompilerMetrics", false)
  if (enableMetrics.get()) {
    val metricsFolderAbsolutePath = project.layout.buildDirectory
      .file("compose-metrics")
      .map { it.asFile.absolutePath }
      .get()

    kotlin {
      compilerOptions {
        freeCompilerArgs.addAll(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsFolderAbsolutePath",
        )
      }
    }
  }

  val enableReports = project.booleanProperty("compose.enableCompilerReports", false)
  if (enableReports.get()) {
    val reportsFolderAbsolutePath = project.layout.buildDirectory
      .file("compose-reports")
      .map { it.asFile.absolutePath }
      .get()

    kotlin {
      compilerOptions {
        freeCompilerArgs.addAll(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$reportsFolderAbsolutePath",
        )
      }
    }
  }

  composeCompiler {
    // Enable 'strong skipping'
    // https://medium.com/androiddevelopers/jetpack-compose-strong-skipping-mode-explained-cbdb2aa4b900
    enableStrongSkippingMode.set(true)

    // Needed for Layout Inspector to be able to see all of the nodes in the component tree:
    //https://issuetracker.google.com/issues/338842143
    includeSourceInformation.set(true)

    if (project.providers.gradleProperty("tivi.enableComposeCompilerReports").isPresent) {
      val composeReports = layout.buildDirectory.map { it.dir("reports").dir("compose") }
      reportsDestination.set(composeReports)
      metricsDestination.set(composeReports)
    }

    stabilityConfigurationFile.set(rootProject.file("compose-stability.conf"))

    targetKotlinPlatforms.set(
      KotlinPlatformType.entries
        .filterNot { it == KotlinPlatformType.native || it == KotlinPlatformType.jvm || it == KotlinPlatformType.wasm }
        .asIterable(),
    )
  }
}
