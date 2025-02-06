package com.thomaskioko.tvmaniac.plugins

import com.thomaskioko.tvmaniac.extensions.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Locale

class KotlinMultiplatformPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("org.jetbrains.kotlin.multiplatform")
      apply("com.autonomousapps.dependency-analysis")
    }

    configureKotlinJvm()

    extensions.configure<KotlinMultiplatformExtension> {
      applyDefaultHierarchyTemplate()

      if (pluginManager.hasPlugin("com.android.library")) {
        androidTarget()
      }

      jvm()

      listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
      ).forEach { target ->
        target.binaries.framework {
          baseName = path.substring(1).replace(':', '-')
        }
      }

      sourceSets.all {
        languageSettings {
          listOf(
            "kotlin.RequiresOptIn",
            "kotlin.experimental.ExperimentalObjCName",
            "kotlin.time.ExperimentalTime",
            "kotlinx.coroutines.ExperimentalCoroutinesApi",
          ).forEach { optIn(it) }
        }
      }

      targets.withType<KotlinNativeTarget>().configureEach {

        binaries.all {
          linkerOpts("-lsqlite3")
        }

        compilations.configureEach {
          compileTaskProvider.configure {
            compilerOptions {
              freeCompilerArgs.add("-Xallocator=custom")
              freeCompilerArgs.add("-XXLanguage:+ImplicitSignedToUnsignedIntegerConversion")
              freeCompilerArgs.add("-Xadd-light-debug=enable")
              freeCompilerArgs.add("-Xexpect-actual-classes")

              freeCompilerArgs.addAll(
                "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                "-opt-in=kotlinx.cinterop.BetaInteropApi",
              )
            }
          }
        }
      }
    }
  }
}

fun Project.addLanguageArgs(vararg args: String) {
  extensions.configure<KotlinMultiplatformExtension> {
    sourceSets.all {
      languageSettings {
        args.forEach { optIn(it) }
      }
    }
  }
}

fun Project.addKspDependencyForAllTargets(dependencyNotation: Any) = addKspDependencyForAllTargets("", dependencyNotation)

private fun Project.addKspDependencyForAllTargets(
  configurationNameSuffix: String,
  dependencyNotation: Any,
) {
  val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()
  val kspTargets = kmpExtension.targets.names.map { targetName ->
    targetName.replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
    }
  }
  dependencies {
    kspTargets
      .asSequence()
      .map { target ->
        if (target == "Metadata") "CommonMainMetadata" else target
      }
      .forEach { targetConfigSuffix ->
        add("ksp${targetConfigSuffix}$configurationNameSuffix", dependencyNotation)
      }
  }
}
