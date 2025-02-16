package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.plugin.utils.compilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlinMultiplatform
import com.thomaskioko.tvmaniac.gradle.plugin.extensions.MultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import com.thomaskioko.tvmaniac.gradle.plugin.utils.defaultTestSetup

/**
 * A base plugin that configures a Kotlin Multiplatform project with common defaults.
 */
public abstract class KotlinMultiplatformPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.plugins.apply("org.jetbrains.kotlin.multiplatform")
    target.plugins.apply(BasePlugin::class.java)

    target.baseExtension.extensions.create("multiplatform", MultiplatformExtension::class.java)

    target.kotlinMultiplatform {
      applyDefaultHierarchyTemplate()

      if (target.pluginManager.hasPlugin("com.android.library")) {
        androidTarget()
      }

      jvm()

      iosArm64()
      iosX64()
      iosSimulatorArm64()
    }

    target.kotlin {
      compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
      }
    }

    target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)
  }
}
