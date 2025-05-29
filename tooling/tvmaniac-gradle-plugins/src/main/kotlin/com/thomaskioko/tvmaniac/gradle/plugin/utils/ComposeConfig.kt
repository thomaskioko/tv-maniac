package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType


internal fun Project.setupCompose() {
  plugins.apply("org.jetbrains.kotlin.plugin.compose")

  val enableMetrics = project.booleanProperty("compose.enableCompilerMetrics", false)
  val enableReports = project.booleanProperty("compose.enableCompilerReports", false)

  composeCompiler {
    // Needed for Layout Inspector to be able to see all of the nodes in the component tree:
    //https://issuetracker.google.com/issues/338842143
    includeSourceInformation.set(true)

      if (enableMetrics.get()) {
          val metricsFolder = layout.buildDirectory.map { it.dir("compose-metrics") }
          metricsDestination.set(metricsFolder)
      }

      if (enableReports.get()) {
          val reportsFolder = layout.buildDirectory.map { it.dir("compose-reports") }
          reportsDestination.set(reportsFolder)
      }

      if (project.providers.gradleProperty("compose.enableComposeCompilerReports").isPresent) {
      val composeReports = layout.buildDirectory.map { it.dir("reports").dir("compose") }

          if (!enableReports.get()) {
              reportsDestination.set(composeReports)
          }

          if (!enableMetrics.get()) {
              metricsDestination.set(composeReports)
          }
    }

      val stabilityFile = project.layout.projectDirectory.file(rootProject.file("compose-stability.conf").absolutePath)
      stabilityConfigurationFiles.add(stabilityFile)

    targetKotlinPlatforms.set(
      KotlinPlatformType.entries
        .filterNot { it == KotlinPlatformType.native || it == KotlinPlatformType.jvm || it == KotlinPlatformType.wasm }
        .asIterable(),
    )
  }
}
