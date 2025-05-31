package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.plugin.extensions.MultiplatformExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.compilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.defaultTestSetup
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
            iosSimulatorArm64()

            targets.withType(KotlinNativeTarget::class.java).configureEach {
                it.binaries.all { framework ->
                    framework.linkerOpts("-lsqlite3")
                }

                it.compilations.configureEach { compilation ->
                    compilation.compileTaskProvider.configure { compileTask ->
                        compileTask.compilerOptions.freeCompilerArgs.addAll(
                            "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                            "-opt-in=kotlinx.cinterop.BetaInteropApi",
                            "-Xallocator=custom",
                            "-Xadd-light-debug=enable",
                            "-Xexpect-actual-classes",
                        )
                    }
                }
            }
        }

        target.kotlin {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }

        target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)
    }
}
