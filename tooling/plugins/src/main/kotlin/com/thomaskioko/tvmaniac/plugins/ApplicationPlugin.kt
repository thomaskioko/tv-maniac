package com.thomaskioko.tvmaniac.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.thomaskioko.tvmaniac.extensions.Versions
import com.thomaskioko.tvmaniac.extensions.configureAndroid
import com.thomaskioko.tvmaniac.extensions.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ApplicationPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
      }

      extensions.configure<ApplicationExtension> {
        defaultConfig {
          targetSdk = Versions.TARGET_SDK
        }

        buildFeatures.buildConfig = true

        configureAndroid()
        configureAndroidCompose(this)
      }
    }
  }
}
