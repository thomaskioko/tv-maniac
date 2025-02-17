package com.thomaskioko.tvmaniac.gradle.plugin.extensions

import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.gradle.plugin.AndroidPlugin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlinMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

public abstract class MultiplatformExtension(private val project: Project) {
  @JvmOverloads
  public fun addJvmTarget(configure: KotlinJvmTarget.() -> Unit = { }) {
    project.kotlinMultiplatform {
      jvm(configure = configure)
    }
  }

  @JvmOverloads
  public fun addAndroidTarget(
      configure: KotlinAndroidTarget.() -> Unit = { },
      androidConfig: (LibraryExtension.() -> Unit) = { },
  ) {

    if (!project.plugins.hasPlugin("com.android.library")) {
      project.plugins.apply("com.android.library")
    }
    project.plugins.apply(AndroidPlugin::class.java)

    project.kotlinMultiplatform {
      androidTarget {
        configure()
      }
    }

    project.extensions.configure(LibraryExtension::class.java, androidConfig)

  }

  @JvmOverloads
  public fun addIosTargetsWithXcFramework(
      frameworkName: String,
      configure: KotlinNativeTarget.(Framework) -> Unit = { },
  ) {
    val xcFramework = XCFrameworkConfig(project, frameworkName)

    project.kotlinMultiplatform {


      targets.withType(KotlinNativeTarget::class.java).configureEach {
        it.binaries.framework {
          baseName = frameworkName

          xcFramework.add(this)
          it.configure(this)
        }
      }
    }
  }
}
