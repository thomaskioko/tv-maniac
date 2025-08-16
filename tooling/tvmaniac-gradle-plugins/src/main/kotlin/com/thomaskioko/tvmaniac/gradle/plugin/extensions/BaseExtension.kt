package com.thomaskioko.tvmaniac.gradle.plugin.extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.androidLibrary
import com.thomaskioko.tvmaniac.gradle.plugin.AndroidMultiplatformPlugin
import com.thomaskioko.tvmaniac.gradle.plugin.AndroidPlugin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.addImplementationDependency
import com.thomaskioko.tvmaniac.gradle.plugin.utils.compilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getDependency
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmCompilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmTarget
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlin
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlinMultiplatform
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig

public abstract class BaseExtension(private val project: Project) : ExtensionAware {
    public fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    public fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptions {
                optIn.addAll(*classes)
            }
        }
    }

    public fun useSerialization() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.serialization")

        project.addImplementationDependency(project.getDependency("kotlin-serialization-core"))
    }

    public fun useDependencyInjection() {
        project.plugins.apply("dev.zacsweers.metro")

        project.addImplementationDependency(project.getDependency("metro-runtime"))

    }

    public fun android(configure: AndroidExtension.() -> Unit) {
        val androidExtension = extensions.findByType(AndroidExtension::class.java)
            ?: throw IllegalStateException("Android extension not found. Did you call addAndroidTarget()?")
        androidExtension.configure()
    }

    @Deprecated("Use addAndroidMultiplatformTarget instead")
    @JvmOverloads
    public fun addAndroidTarget(
        configure: AndroidExtension.() -> Unit = { },
        libraryConfiguration: LibraryExtension.() -> Unit = { },
    ) {
        project.plugins.apply(AndroidPlugin::class.java)

        project.kotlinMultiplatform {
            androidTarget()
        }

        project.extensions.configure(LibraryExtension::class.java) { extension ->
            extension.libraryConfiguration()
        }

        val androidExtension = extensions.findByType(AndroidExtension::class.java)
            ?: throw IllegalStateException("Android extension not found. AndroidPlugin should have created it.")
        androidExtension.configure()
    }

    @Suppress("UnstableApiUsage")
    @JvmOverloads
    public fun addAndroidMultiplatformTarget(
        withDeviceTestBuilder: Boolean = false,
        enableAndroidResources: Boolean = false,
        withJava: Boolean = false,
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
        applyPlugin: Boolean = true,
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
