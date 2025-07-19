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
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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


                freeCompilerArgs.addAll(
                    // https://youtrack.jetbrains.com/issue/KT-73255
                    "-Xannotation-default-target=param-property",
                    // https://kotlinlang.org/docs/whatsnew2020.html#data-class-copy-function-to-have-the-same-visibility-as-constructor
                    "-Xconsistent-data-class-copy-visibility",
                    // Enable 2.2.0 feature previews
                    "-Xcontext-parameters",
                    "-Xcontext-sensitive-resolution",
                    "-Xannotation-target-all",
                    // opt in to experimental stdlib apis
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-opt-in=kotlin.uuid.ExperimentalUuidApi",
                )

                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(project.jvmTarget)
                    jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)

                    freeCompilerArgs.addAll(
                        // https://youtrack.jetbrains.com/issue/KT-22292
                        "-Xassertions=jvm",
                        // Enabling default nullability annotations
                        "-Xjsr305=strict",
                        // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
                        "-Xjspecify-annotations=strict",
                        // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T => T & Any)
                        "-Xenhance-type-parameter-types-to-def-not-null",
                        // https://kotlinlang.org/docs/whatsnew-eap.html#support-for-reading-and-writing-annotations-in-kotlin-metadata
                        "-Xannotations-in-metadata",
                    )
                }
            }
        }
    }
}
