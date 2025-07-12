package com.thomaskioko.tvmaniac.gradle.plugin.extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.dsl.androidLibrary
import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.gradle.plugin.AndroidMultiplatformPlugin
import com.thomaskioko.tvmaniac.gradle.plugin.AndroidPlugin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmCompilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmTarget
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

    @Deprecated("Use addAndroidMultiplatformTarget instead")
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

    /**
     * Configures an Android target with various options.
     *
     * @param withDeviceTestBuilder Include device test builder configuration
     * @param withJava Include Java support
     * @param action Custom configuration to apply
     */
    @Suppress("UnstableApiUsage")
    @JvmOverloads
    public fun addAndroidMultiplatformTarget(
        withDeviceTestBuilder: Boolean = false,
        withJava: Boolean = false,
        enableAndroidResources: Boolean = true,
        action: KotlinMultiplatformAndroidLibraryTarget.() -> Unit = { },
    ) {
        project.plugins.apply(AndroidMultiplatformPlugin::class.java)

        project.kotlinMultiplatform {
            androidLibrary {
                experimentalProperties["android.experimental.kmp.enableAndroidResources"] = enableAndroidResources
                if (withDeviceTestBuilder) {
                    withDeviceTestBuilder {
                        sourceSetTreeName = "test"
                    }.configure {
                        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }
                }

                if (withJava) {
                    jvmCompilerOptions {
                        withJava()
                        jvmTarget.set(project.jvmTarget)
                    }
                }

                action()
            }
        }
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
