package com.thomaskioko.tvmaniac.gradle.plugin

import com.autonomousapps.DependencyAnalysisExtension
import com.osacky.doctor.DoctorExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.booleanProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.internal.jvm.inspection.JvmVendor

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
      it.jvmVendor.set(JvmVendor.KnownJvmVendor.AZUL.name)
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
              "org.jetbrains.kotlin:kotlin-stdlib", // added by the Kotlin plugin
            )
          }

          project.onRedundantPlugins {
            it.severity("fail")
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

        structure.bundle("androidx-compose-runtime") {
          it.primary("androidx.compose.runtime:runtime")
          it.includeGroup("androidx.compose.runtime")
        }

        structure.bundle("androidx-compose-ui") {
          it.primary("androidx.compose.ui:ui")
          it.includeGroup("androidx.compose.ui")
          it.includeDependency("androidx.compose.runtime:runtime-saveable")
        }

        structure.bundle("compose-animation") {
          it.primary("androidx.compose.animation:animation")
          it.includeGroup("androidx.compose.animation")
        }

        structure.bundle("androidx-compose-foundation") {
          it.primary("androidx.compose.foundation:foundation")
          it.includeGroup("androidx.compose.foundation")
        }

        structure.bundle("androidx-compose-material") {
          it.primary("androidx.compose.material:material")
          it.includeGroup("androidx.compose.material")
        }

        structure.bundle("androidx-compose-material3") {
          it.primary("androidx.compose.material3:material3")
          it.includeGroup("androidx.compose.material3")
        }

        structure.bundle("coil") {
          it.includeDependency("io.coil-kt:coil")
          it.includeDependency("io.coil-kt:coil-base")
        }

        structure.bundle("coil-compose") {
          it.primary("io.coil-kt:coil-compose")
          it.includeDependency("io.coil-kt:coil-compose")
          it.includeDependency("io.coil-kt:coil-compose-base")
        }

        structure.bundle("compose-runtime") {
          it.primary("androidx.compose.runtime:runtime")
          it.includeGroup("androidx.compose.runtime")
        }

        structure.bundle("roborazzi") {
          it.primary("io.github.takahirom.roborazzi:roborazzi-core")
          it.includeGroup("io.github.takahirom.roborazzi:roborazzi-core")
        }
      }
    }
  }
}
