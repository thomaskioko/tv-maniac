package com.thomaskioko.tvmaniac.plugins

import com.diffplug.gradle.spotless.SpotlessExtension
import com.thomaskioko.tvmaniac.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SpotlessPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      pluginManager.apply("com.diffplug.spotless")

      val ktfmtVersion = libs.findVersion("ktfmt").get().requiredVersion
      extensions.configure<SpotlessExtension> {
        kotlin {
          ktfmt(ktfmtVersion).googleStyle()
          target("src/**/*.kt")
          targetExclude("${layout.buildDirectory}/**/*.kt")
        }
        kotlinGradle {
          ktfmt(ktfmtVersion).googleStyle()
          target("*.kts")
          targetExclude("${layout.buildDirectory}/**/*.kts")
        }
        format("xml") {
          target("src/**/*.xml")
          targetExclude("**/build/", ".idea/")
          trimTrailingWhitespace()
          endWithNewline()
        }
      }
    }
  }
}
