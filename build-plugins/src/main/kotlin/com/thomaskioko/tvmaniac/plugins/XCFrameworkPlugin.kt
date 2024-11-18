package com.thomaskioko.tvmaniac.plugins

import com.thomaskioko.tvmaniac.extensions.NativeTargetType
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.GROUP_NAME
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.capitalizedName
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.intermediatesDir
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.nativeBuildTargetTypes
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.nativeBuildType
import com.thomaskioko.tvmaniac.extensions.XcodeBuildEnvironment.nativeTargetType
import com.thomaskioko.tvmaniac.extensions.nativeFrameworks
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.listDirectoryEntries

/**
 * Configuration options for XCFramework generation.
 */
interface XCFrameworkExtension {
  /** Name of the generated framework */
  val frameworkName: Property<String>

  /** Output directory path relative to project root */
  val outputPath: Property<String>

  /** Whether to clean intermediate files during build */
  val cleanIntermediate: Property<Boolean>
}

/**
 * Gradle plugin for managing XCFramework generation and distribution for iOS targets.
 * This plugin handles:
 * - Assembly of XCFrameworks for different build types (Debug/Release)
 * - Copying frameworks to the iOS module directory
 * - Cleanup of existing frameworks and intermediate files
 * - Support for different native target types (Device/Simulator)
 */
abstract class XCFrameworkPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    validateConfiguration()

    // Create and configure extension with defaults
    val extension = extensions.create<XCFrameworkExtension>("xcframework").apply {
      frameworkName.convention("TvManiac.xcframework")
      outputPath.convention("ios/Modules/TvManiacKit")
      cleanIntermediate.convention(true)
    }

    // Register default task
    registerCopyXCFrameworkTask(
      nativeBuildType = nativeBuildType,
      targetType = nativeTargetType,
      extension = extension,
    )

    // Register tasks for each build type/target combination (Debug/Release and Device/Simulator)
    nativeBuildTargetTypes.forEach { (buildType, targetType) ->
      registerCopyXCFrameworkTask(
        nativeBuildType = buildType,
        targetType = targetType,
        buildInfix = "${buildType.capitalizedName}${targetType.capitalizedName}",
        extension = extension,
      )
    }
  }

  @OptIn(ExperimentalPathApi::class)
  private fun Project.registerCopyXCFrameworkTask(
    nativeBuildType: NativeBuildType,
    targetType: NativeTargetType,
    buildInfix: String = "",
    extension: XCFrameworkExtension,
  ): TaskProvider<Copy> {
    val multiplatformExtension = extensions.findByType<KotlinMultiplatformExtension>()
      ?: throw GradleException("KotlinMultiplatformExtension not found. Apply Kotlin Multiplatform plugin first.")

    val projectName = name
    val projectDir = rootProject.projectDir

    // Register assembly task
    val assembleXCFrameworkTask = tasks.register<XCFrameworkTask>("assemble${buildInfix}XCFramework") {
      group = GROUP_NAME
      buildType = nativeBuildType

      val frameworks = multiplatformExtension.nativeFrameworks(nativeBuildType, targetType.targets)
      from(*frameworks.toTypedArray())
    }

    // Register copy task
    return tasks.register<Copy>("copy${buildInfix}XCFramework") {
      group = GROUP_NAME
      description = "Copies the $buildInfix XCFramework to ${extension.outputPath.get()}"

      val outputDir = projectDir.resolve(
        "${extension.outputPath.get()}/${extension.frameworkName.get()}",
      )

      dependsOn(assembleXCFrameworkTask)

      from(
        assembleXCFrameworkTask.map {
          it.outputDir.resolve(nativeBuildType.getName())
            .resolve("$projectName.xcframework")
        },
      )

      into(outputDir)

      doFirst {
        logger.lifecycle("Cleaning existing XCFramework at: ${outputDir.absolutePath}")
        outputDir.deleteRecursively()

        if (extension.cleanIntermediate.get()) {
          logger.lifecycle("Cleaning intermediates for $projectName")
          intermediatesDir?.toPath()?.listDirectoryEntries("${extension.frameworkName.get()}*")?.forEach {
            it.deleteRecursively()
            logger.lifecycle("Cleaned intermediate files: ${it.fileName}")
          }
        }
      }
    }
  }

  private fun Project.validateConfiguration() {
    if (!plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
      throw GradleException("The Kotlin Multiplatform plugin must be applied before the XCFramework plugin")
    }

    if (!rootProject.file("ios").exists()) {
      throw GradleException("iOS directory not found. Expected at: ${rootProject.file("ios").absolutePath}")
    }
  }
}
