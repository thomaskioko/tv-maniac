package com.thomaskioko.tvmaniac.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions


internal fun Project.configureAndroidCompose(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
  commonExtension.apply {

    defaultConfig {
      minSdk = Versions.MIN_SDK
    }

    buildFeatures {
      compose = true
    }

    composeOptions {
      kotlinCompilerExtensionVersion = libs.findVersion("composecompiler").get().toString()
    }

    configureKotlinJvm()

    dependencies {
      val bom = libs.findLibrary("androidx-compose-bom").get()

      add("implementation", platform(bom))
      add("lintChecks", libs.findLibrary("lint-compose").get())
    }
  }
}
