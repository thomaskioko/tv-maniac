package com.thomaskioko.tvmaniac.gradle.plugin.checks

import com.diffplug.gradle.spotless.SpotlessExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpotlessPlugin : Plugin<Project> {
  override fun apply(project: Project): Unit = with(project) {
    pluginManager.apply("com.diffplug.spotless")

    val ktfmtVersion = libs.findVersion("ktfmt").get().requiredVersion
    extensions.configure(SpotlessExtension::class.java) { extension ->
      with(extension) {
        kotlin {
          it.ktfmt(ktfmtVersion).googleStyle()
          it.target("src/**/*.kt")
          it.targetExclude("${layout.buildDirectory}/**/*.kt")
        }
        kotlinGradle {
          it.ktfmt(ktfmtVersion).googleStyle()
          it.target("*.kts")
          it.targetExclude("${layout.buildDirectory}/**/*.kts")
        }
        format("xml") {
          it.target("src/**/*.xml")
          it.targetExclude("**/build/", ".idea/")
          it.trimTrailingWhitespace()
          it.endWithNewline()
        }
      }
    }
  }
}
