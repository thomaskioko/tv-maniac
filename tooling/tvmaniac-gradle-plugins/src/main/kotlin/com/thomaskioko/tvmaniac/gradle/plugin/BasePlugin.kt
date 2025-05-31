package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.plugin.extensions.BaseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.compilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getVersionOrNull
import com.thomaskioko.tvmaniac.gradle.plugin.utils.java
import com.thomaskioko.tvmaniac.gradle.plugin.utils.javaTarget
import com.thomaskioko.tvmaniac.gradle.plugin.utils.javaToolchainVersion
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmTarget
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

/**
 * Base class for all plugins in the project. This class provides common configurations that are shared across multiple plugins.
 */
public abstract class BasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.autonomousapps.dependency-analysis")
        target.plugins.apply("com.thomaskioko.tvmaniac.gradle.spotless")

        target.extensions.create("tvmaniac", BaseExtension::class.java)

        target.makeJarsReproducible()
        target.configureJava()
        target.configureKotlin()
    }

    private fun Project.makeJarsReproducible() {
        tasks.withType(Jar::class.java).configureEach {
            it.isReproducibleFileOrder = true
            it.isPreserveFileTimestamps = false
        }
    }

    internal fun Project.configureJava() {
        java {
            toolchain {
                it.languageVersion.set(javaToolchainVersion)
                it.vendor.set(JvmVendorSpec.AZUL)
            }
        }
    }

    private fun Project.configureKotlin() {
        kotlin {
            jvmToolchain { toolchain ->
                toolchain.languageVersion.set(javaToolchainVersion)
                toolchain.vendor.set(JvmVendorSpec.AZUL)
            }

            val isAndroid = this is KotlinAndroidProjectExtension

            compilerOptions {
                val version = getVersionOrNull("kotlin-language")
                    ?.let(KotlinVersion.Companion::fromVersion) ?: KotlinVersion.Companion.DEFAULT
                languageVersion.set(version)

                // In this mode, some deprecations and bug-fixes for unstable code take effect immediately.
                progressiveMode.set(version >= KotlinVersion.Companion.DEFAULT)


                // Enable new Kotlin 2.1
                freeCompilerArgs.addAll(
                    "-Xnon-local-break-continue",
                    "-Xmulti-dollar-interpolation",
                    "-Xexpect-actual-classes",
                    "-Xwhen-guards",
                )

                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)

                    freeCompilerArgs.addAll(
                        "-Xjvm-default=all",
                        "-Xassertions=jvm",
                        // https://kotlinlang.org/docs/whatsnew2020.html#data-class-copy-function-to-have-the-same-visibility-as-constructor
                        "-Xconsistent-data-class-copy-visibility",
                    )

                    if (!isAndroid) {
                        freeCompilerArgs.add("-Xjdk-release=${project.javaTarget}")
                    }
                }
            }
        }
    }
}
