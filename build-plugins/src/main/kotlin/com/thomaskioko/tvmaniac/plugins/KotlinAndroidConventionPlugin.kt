package com.thomaskioko.tvmaniac.plugins

import com.thomaskioko.tvmaniac.extensions.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinAndroidConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.kotlin.android")
      }

      configureKotlinJvm()
    }
  }
}
