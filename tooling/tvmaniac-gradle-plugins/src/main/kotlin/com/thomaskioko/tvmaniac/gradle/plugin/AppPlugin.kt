package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.plugin.extensions.AppExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.androidApp
import com.thomaskioko.tvmaniac.gradle.plugin.utils.androidComponents
import com.thomaskioko.tvmaniac.gradle.plugin.utils.androidExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.disableAndroidApplicationTasks
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getVersion
import com.thomaskioko.tvmaniac.gradle.plugin.utils.stringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")
        target.plugins.apply("androidx.baselineprofile")
        target.plugins.apply(AndroidPlugin::class.java)

        target.baseExtension.extensions.create("app", AppExtension::class.java)

        target.androidExtension.enableBuildConfig()

        target.androidApp {
            defaultConfig {
                versionCode = target.getVersion("app-version-code").toInt()
                versionName = target.getVersion("app-version-name")
                manifestPlaceholders["appAuthRedirectScheme"] = "tvmaniac"
            }

            signingConfigs {
                named("debug") {
                    val debugKeystore = target.rootProject.file("gradle/debug.keystore")
                    if (debugKeystore.exists()) {
                        it.storeFile = debugKeystore
                    }
                    // enable v4 signing for incremental installs on Android 12
                    it.enableV3Signing = true
                    it.enableV4Signing = true
                }

                val keyStoreFile = target.stringProperty("releaseStoreFile")
                if (keyStoreFile.isPresent) {
                    register("release") {
                        it.storeFile = target.rootProject.file(keyStoreFile.get())
                        it.storePassword = target.stringProperty("releaseStorePassword").get()
                        it.keyAlias = target.stringProperty("rReleaseKeyAlias").get()
                        it.keyPassword = target.stringProperty("releaseKeyPassword").get()
                        it.enableV3Signing = true
                        it.enableV4Signing = true
                    }
                }
            }

            buildTypes {
                named("debug") {
                    it.applicationIdSuffix = ".debug"
                    it.signingConfig = signingConfigs.getByName("debug")
                }

                named("release") {
                    it.signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
                }
            }

            lint {
                baseline = target.file("lint-baseline.xml")
            }

            @Suppress("UnstableApiUsage")
            testOptions {
                // include test resources in app module to be able to test the manifest
                unitTests.isIncludeAndroidResources = true

            }
        }

        target.androidComponents {
            onVariants(selector().withBuildType("release")) {
                it.packaging.resources.excludes.addAll(
                    // produced by Kotlin for each module/library that uses it, they are not needed inside an app
                    "kotlin/*.kotlin_builtins",
                    "kotlin/**/*.kotlin_builtins",
                    "META-INF/*.kotlin_module",
                    // these are part of AndroidX and only contain version information
                    "META-INF/*.version",
                    // these contain build meta data for various Google Play and Firebase libraries
                    "core.properties",
                    "billing.properties",
                    "build-data.properties",
                    "play-services-*.properties",
                    "firebase-*.properties",
                    "transport-*.properties",
                    // metadata with which Kotlin version the app was built
                    "kotlin-tooling-metadata.json",
                    // various license files or other random files that were packaged in some libraries
                    "asm-license.txt",
                    "LICENSE",
                    "LICENSE_OFL",
                    "LICENSE_UNICODE",
                    "LICENSE.txt",
                    "NOTICE",
                    "README.txt",
                    "META-INF/LICENSE.txt",
                    "META-INF/LICENSE-FIREBASE.txt",
                    "META-INF/LICENSE",
                    "META-INF/NOTICE.txt",
                    "META-INF/NOTICE",
                    "META-INF/DEPENDENCIES.txt",
                )
            }
        }

        target.disableAndroidApplicationTasks()
    }
}
