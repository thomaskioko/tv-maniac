package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64
import org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64
import java.io.File
import java.util.Locale

internal enum class NativeTargetType(
  val targets: List<KonanTarget>,
  private val platformName: String,
) {
  DEVICE(listOf(IOS_ARM64), "iphoneos"),
  SIMULATOR(listOf(IOS_SIMULATOR_ARM64), "iphonesimulator");

  val capitalizedName: String = name.lowercase(Locale.ENGLISH).replaceFirstChar { it.uppercaseChar() }

  companion object {
    fun fromPlatformName(platformName: String?): NativeTargetType =
      NativeTargetType.entries.firstOrNull { it.platformName == platformName?.lowercase() } ?: SIMULATOR
  }
}

internal object XcodeBuildEnvironment {
  const val GROUP_NAME = "tvmaniac"

  val nativeBuildType: NativeBuildType
    get() = when (System.getenv("CONFIGURATION")?.lowercase()) {
      "release" -> NativeBuildType.RELEASE
      else -> NativeBuildType.DEBUG
    }

  val nativeTargetType: NativeTargetType
    get() = NativeTargetType.fromPlatformName(
      System.getenv("RUN_DESTINATION_DEVICE_PLATFORM_NAME"),
    )

  val intermediatesDir: File?
    get() = try {
      System.getenv("OBJROOT")?.let { File(it) }
    } catch (e: NoSuchFileException) {
      null
    }

  val nativeBuildTargetTypes: List<Pair<NativeBuildType, NativeTargetType>>
    get() = NativeBuildType.values().flatMap { buildType ->
      NativeTargetType.values().map { target ->
        buildType to target
      }
    }

  val NativeBuildType.capitalizedName: String
    get() = getName().replaceFirstChar { it.uppercaseChar() }
}


internal fun KotlinMultiplatformExtension.nativeFrameworks(
  buildType: NativeBuildType,
  targets: List<KonanTarget>,
): List<Framework> = this.targets
  .filterIsInstance<KotlinNativeTarget>()
  .filter { targets.contains(it.konanTarget) }
  .flatMap { it.binaries.filterIsInstance<Framework>() }
  .filter { it.buildType == buildType }
