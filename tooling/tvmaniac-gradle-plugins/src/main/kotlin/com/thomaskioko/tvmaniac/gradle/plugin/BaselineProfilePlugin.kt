package com.thomaskioko.tvmaniac.gradle.plugin

import androidx.baselineprofile.gradle.producer.BaselineProfileProducerExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import com.android.build.gradle.TestExtension
import com.thomaskioko.tvmaniac.gradle.plugin.extensions.AndroidExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * `BaselineProfilePlugin` is a Gradle plugin that configures a project for generating Baseline Profiles for Android applications.
 */
public class BaselineProfilePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.apply("com.android.test")
    target.plugins.apply("androidx.baselineprofile")
    target.plugins.apply("org.jetbrains.kotlin.android")
    target.plugins.apply(BasePlugin::class.java)

    target.baseExtension.extensions.create("benchmark", AndroidExtension::class.java)

    target.androidSetup()
    target.componentsConfiguration()
  }

  @Suppress("UnstableApiUsage")
  private fun Project.componentsConfiguration() {

    extensions.configure(TestExtension::class.java) { extension ->
      extension.defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      extension.targetProjectPath = ":app"
    }

    extensions.configure(BaselineProfileProducerExtension::class.java) {
      it.managedDevices += "pixel6Api34"
      it.useConnectedDevices = false
      it.enableEmulatorDisplay = false
    }

    extensions.configure(TestAndroidComponentsExtension::class.java) { components ->
      components.onVariants { variant ->
        val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
        variant.instrumentationRunnerArguments.put(
          "targetAppId",
          variant.testedApks.map { artifactsLoader.load(it)?.applicationId },
        )
      }
    }
  }
}

