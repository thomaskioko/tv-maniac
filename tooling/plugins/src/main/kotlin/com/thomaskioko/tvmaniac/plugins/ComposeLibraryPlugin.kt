package com.thomaskioko.tvmaniac.plugins

import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.Versions
import com.thomaskioko.tvmaniac.extensions.configureAndroidCompose
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ComposeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = Versions.COMPILE_SDK

                configureAndroidCompose(this)
                configureFlavors(this)
            }

          tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
              freeCompilerArgs = freeCompilerArgs +
                listOf(
                  "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                  "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                  "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                  "-opt-in=dev.chrisbanes.snapper.ExperimentalSnapperApi",
                  "-opt-in=com.github.takahirom.roborazzi.ExperimentalRoborazziApi",
                  "-opt-in=kotlin.RequiresOptIn",
                )
            }
          }
        }
    }
}
