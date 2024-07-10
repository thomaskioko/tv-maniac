package com.thomaskioko.tvmaniac.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


internal fun Project.configureAndroidCompose(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
  commonExtension.apply {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
      minSdk = Versions.MIN_SDK
      manifestPlaceholders["appAuthRedirectScheme"] = "empty"
    }

    buildFeatures.compose = true

    compileOptions.isCoreLibraryDesugaringEnabled = true

    testOptions {
      unitTests {
        isIncludeAndroidResources = true
      }
    }

    configureComposeCompiler()
    configureKotlinJvm()

    dependencies {
      val bom = libs.findLibrary("androidx-compose-bom").get()

      add("implementation", platform(bom))
      add("lintChecks", libs.findLibrary("lint-compose").get())
      add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
    }

    tasks.withType<KotlinCompile>().configureEach {
      compilerOptions {
        freeCompilerArgs.addAll(
          listOf(
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=com.github.takahirom.roborazzi.ExperimentalRoborazziApi",
            "-opt-in=dev.chrisbanes.snapper.ExperimentalSnapperApi",
            "-opt-in=kotlin.RequiresOptIn",
          )
        )
      }
    }
  }
}

private fun Project.configureComposeCompiler() {
  extensions.configure<ComposeCompilerGradlePluginExtension> {
    // Enable 'strong skipping'
    // https://medium.com/androiddevelopers/jetpack-compose-strong-skipping-mode-explained-cbdb2aa4b900
    enableStrongSkippingMode.set(true)

    // Needed for Layout Inspector to be able to see all of the nodes in the component tree:
    //https://issuetracker.google.com/issues/338842143
    includeSourceInformation.set(true)
  }
}
