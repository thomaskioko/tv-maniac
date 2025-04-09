package com.thomaskioko.tvmaniac.gradle.plugin

import com.autonomousapps.DependencyAnalysisExtension
import com.osacky.doctor.DoctorExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.booleanProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JvmVendorSpec

/**
 * `RootPlugin` is a base Gradle plugin that configures common settings for all subprojects.
 */
public abstract class RootPlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = with(target) {
    plugins.apply("com.autonomousapps.dependency-analysis")

    configureDaemonToolchainTask()
    configureDependencyAnalysis()
    configureGradleDoctor()

  }

  @Suppress("UnstableApiUsage")
  private fun Project.configureDaemonToolchainTask() {
    tasks.withType(UpdateDaemonJvm::class.java).configureEach {
      it.vendor.set(JvmVendorSpec.AZUL)
    }
  }

  private fun Project.configureGradleDoctor() {
    pluginManager.withPlugin("com.osacky.doctor") {
      extensions.configure(DoctorExtension::class.java) { doctor ->
        with(doctor) {
          /**
           * Warn when not using parallel GC. Parallel GC is faster for build type tasks and is no longer the default in Java 9+.
           */
          warnWhenNotUsingParallelGC.set(false)
          /**
           * By default, Gradle treats empty directories as inputs to compilation tasks. This can cause cache misses.
           */
          failOnEmptyDirectories.set(true)

          /**
           * Do not allow building all apps simultaneously. This is likely not what the user intended.
           */
          allowBuildingAllAndroidAppsSimultaneously.set(false)

          /**
           * Warn if using Android Jetifier. It slows down builds.
           */
          warnWhenJetifierEnabled.set(true)

          /**
           * The level at which to warn when a build spends more than this percent garbage collecting.
           */
          GCWarningThreshold.set(0.10f)

          javaHome { handler ->
            with(handler) {
              /**
               * Ensure that we are using JAVA_HOME to build with this Gradle.
               */
              ensureJavaHomeMatches.set(true)

              /**
               * Ensure we have JAVA_HOME set.
               */
              ensureJavaHomeIsSet.set(true)

              /**
               * Fail on any `JAVA_HOME` issues.
               */
              failOnError.set(booleanProperty("java.toolchains.strict", false))

            }
          }
        }
      }
    }
  }

  private fun Project.configureDependencyAnalysis() {
    extensions.configure(DependencyAnalysisExtension::class.java) { analysis ->
      analysis.issues { issues ->
        issues.all { project ->

          project.onIncorrectConfiguration {
            it.exclude(
              "org.jetbrains.kotlin:kotlin-stdlib",
              "androidx.core:core-ktx",
              "androidx.lifecycle:lifecycle-runtime-ktx",
              "io.coil-kt:coil-compose"
            )
          }

          project.onRedundantPlugins {
            it.severity("fail")
          }

          project.onUnusedDependencies {
            it.severity("fail")

            it.exclude(
              "io.coil-kt:coil-compose",
              "io.coil-kt:coil-compose-base"
            )
          }

          project.onUsedTransitiveDependencies {
            it.severity("warn")
          }
        }
      }

      analysis.structure { structure ->
        structure.ignoreKtx(true)

        structure.bundle("androidx-lifecycle") {
          it.primary("androidx.lifecycle:lifecycle-runtime")
          it.includeGroup("androidx.lifecycle")
          it.includeGroup("androidx.arch.core")
        }

        structure.bundle("androidx-activity") {
          it.primary("androidx.activity:activity")
          it.includeGroup("androidx.activity")
        }

        structure.bundle("compose-runtime") {
          it.primary("androidx.compose.runtime:runtime")
          it.includeGroup("androidx.compose.runtime")
          it.includeDependency("androidx.compose.runtime:runtime-saveable")
        }

        structure.bundle("compose-ui") {
          it.primary("androidx.compose.ui:ui")
          it.includeGroup("androidx.compose.ui")
          it.includeDependency("androidx.compose.ui:ui-tooling-preview")
        }

        structure.bundle("compose-animation") {
          it.primary("androidx.compose.animation:animation")
          it.includeGroup("androidx.compose.animation")
        }

        structure.bundle("compose-foundation") {
          it.primary("androidx.compose.foundation:foundation")
          it.includeGroup("androidx.compose.foundation")
        }

        structure.bundle("compose-material") {
          it.primary("androidx.compose.material:material")
          it.includeGroup("androidx.compose.material")
          it.includeDependency("androidx.compose.material:material-icons-core")
        }

        structure.bundle("compose-material3") {
          it.primary("androidx.compose.material3:material3")
          it.includeGroup("androidx.compose.material3")
        }

        structure.bundle("coil") {
          it.primary("io.coil-kt:coil")
          it.includeGroup("io.coil-kt")
          it.includeDependency("io.coil-kt:coil-compose")
          it.includeDependency("io.coil-kt:coil-compose-base")
        }

        structure.bundle("ktor") {
          it.primary("io.ktor:ktor-http")
          it.includeGroup("io.ktor")
        }

        structure.bundle("kotlin-coroutines") {
          it.primary("org.jetbrains.kotlinx:kotlinx-coroutines-core")
          it.includeGroup("org.jetbrains.kotlinx")
        }

        structure.bundle("kotlin-collections") {
          it.primary("org.jetbrains.kotlinx:kotlinx-collections-immutable")
          it.includeGroup("org.jetbrains.kotlinx")
        }

        structure.bundle("testing") {
          it.primary("junit:junit")
          it.includeGroup("org.junit.jupiter")
          it.includeGroup("io.mockk")
          it.includeGroup("org.jetbrains.kotlinx.kotlinx-coroutines-test")
          it.includeGroup("org.robolectric")
        }

        structure.bundle("sqldelight") {
          it.primary("com.squareup.sqldelight:runtime")
          it.includeGroup("com.squareup.sqldelight")
        }

        structure.bundle("kermit") {
          it.primary("co.touchlab:kermit")
          it.includeGroup("co.touchlab")
        }

        structure.bundle("roborazzi") {
          it.primary("io.github.takahirom.roborazzi:roborazzi-core")
          it.includeGroup("io.github.takahirom.roborazzi")
        }
      }
    }
  }
}
