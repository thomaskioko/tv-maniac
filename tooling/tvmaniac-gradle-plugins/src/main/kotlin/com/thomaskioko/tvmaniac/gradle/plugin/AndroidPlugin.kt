package com.thomaskioko.tvmaniac.gradle.plugin

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.variant.HasAndroidTestBuilder
import com.android.build.api.variant.HasUnitTestBuilder
import com.thomaskioko.tvmaniac.gradle.plugin.extensions.AndroidExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.addIfNotNull
import com.thomaskioko.tvmaniac.gradle.plugin.utils.android
import com.thomaskioko.tvmaniac.gradle.plugin.utils.androidComponents
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.configure
import com.thomaskioko.tvmaniac.gradle.plugin.utils.defaultTestSetup
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getDependencyOrNull
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getVersion
import com.thomaskioko.tvmaniac.gradle.plugin.utils.javaTargetVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

public abstract class AndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin("com.android.application")) {
            target.plugins.apply("com.android.library")
        }
        if (!target.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            target.plugins.apply("org.jetbrains.kotlin.android")
        }
        target.plugins.apply(BasePlugin::class.java)

        target.baseExtension.extensions.create("android", AndroidExtension::class.java)

        target.androidSetup()
        target.configureLint()
        target.configureUnitTests()
        target.disableAndroidTests()
    }

    private fun Project.configureLint() {
        android {
            lint.configure(project)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configureUnitTests() {
        android {
            testOptions {
                unitTests.all(Test::defaultTestSetup)
            }
        }

        androidComponents {
            beforeVariants(
                selector().withBuildType("release"),
            ) {
                (it as? HasUnitTestBuilder)?.enableUnitTest = false
            }
        }
    }

    private fun Project.disableAndroidTests() {
        androidComponents {
            beforeVariants {
                if (it is HasAndroidTestBuilder) {
                    it.androidTest.enable = false
                }
            }
        }
    }
}

internal fun Project.androidSetup() {
    val desugarLibrary = project.getDependencyOrNull("android-desugarJdkLibs")
    android {
        namespace = pathBasedAndroidNamespace()

        compileSdk = getVersion("android-compile").toInt()
        defaultConfig.minSdk = getVersion("android-min").toInt()
        (defaultConfig as? ApplicationDefaultConfig)?.let {
            it.targetSdk = getVersion("android-target").toInt()
        }

        // default all features to false, they will be enabled through TvManiacAndroidExtension
        buildFeatures {
            viewBinding = false
            resValues = false
            buildConfig = false
            aidl = false
            renderScript = false
            shaders = false
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = desugarLibrary != null
            sourceCompatibility = javaTargetVersion.get()
            targetCompatibility = javaTargetVersion.get()
        }
    }

    dependencies.addIfNotNull("coreLibraryDesugaring", desugarLibrary)
}

internal fun Project.pathBasedAndroidNamespace(): String {
    val transformedPath = path.drop(1)
        .split(":")
        .mapIndexed { index, pathElement ->
            val parts = pathElement.split("-")
            if (index == 0) {
                parts.joinToString(separator = ".")
            } else {
                parts.joinToString(separator = "")
            }
        }
        .joinToString(separator = ".")

    return "com.thomaskioko.tvmaniac.$transformedPath"
}
