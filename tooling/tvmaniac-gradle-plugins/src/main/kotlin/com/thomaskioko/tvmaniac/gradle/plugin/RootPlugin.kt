package com.thomaskioko.tvmaniac.gradle.plugin

import com.autonomousapps.DependencyAnalysisExtension
import com.osacky.doctor.DoctorExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.booleanProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JvmVendorSpec

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
            it.vendor.set(JvmVendorSpec.AZUL)
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
                            "org.jetbrains.kotlin:kotlin-stdlib",
                            "androidx.core:core-ktx",
                            "androidx.lifecycle:lifecycle-runtime-ktx",
                            "io.coil-kt:coil-compose",
                        )
                    }

                    project.onRedundantPlugins {
                        it.severity("fail")
                    }

                    project.onUnusedDependencies {
                        it.severity("fail")

                        it.exclude(
                            "io.coil-kt:coil-compose",
                            "io.coil-kt:coil-compose-base",
                            // Exclude androidx.compose.foundation which is used for layout components
                            "androidx.compose.foundation:foundation",
                        )
                    }

                    project.onUsedTransitiveDependencies {
                        it.severity("warn")

                        // Exclude commonly used transitive dependencies that are showing up in the report
                        it.exclude(
                            // Common Kotlin dependencies
                            "org.jetbrains.kotlin:kotlin-stdlib",

                            // Common Compose dependencies
                            "androidx.compose.animation:animation",
                            "androidx.compose.material:material-icons-core",
                            "androidx.compose.ui:ui-tooling-preview",
                            "androidx.compose.ui:ui",

                            // Common libraries
                            "androidx.lifecycle:lifecycle-runtime-compose",
                            "androidx.lifecycle:lifecycle-runtime",
                            "libs.kotlinx.collections",
                            "libs.coroutines.core",
                            "libs.moko.resources",
                            "libs.moko.resources.compose",
                            "libs.androidx.compose.material.icons",

                            // Common Android libraries
                            "androidx.activity:activity",
                            "androidx.paging:paging-common",
                            "androidx.sqlite:sqlite",
                            "androidx.datastore:datastore-core",

                            // Additional dependencies from the report
                            "com.squareup.okhttp3:okhttp",
                            "libs.kotlinx.serialization.json",
                            "libs.sqldelight.driver.android",
                            "libs.sqldelight.runtime",
                            "libs.sqldelight.driver.jvm",
                            "libs.decompose.decompose",
                            "libs.androidx.paging.common",

                            // Common test dependencies
                            "junit:junit",
                            "androidx.junit",
                            "libs.androidx.junit",
                            "io.kotest:kotest-assertions-shared",
                        )
                    }
                }
            }
        }
    }
}

