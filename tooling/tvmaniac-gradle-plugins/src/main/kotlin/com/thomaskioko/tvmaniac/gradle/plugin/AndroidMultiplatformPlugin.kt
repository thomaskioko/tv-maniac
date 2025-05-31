package com.thomaskioko.tvmaniac.gradle.plugin

import com.android.build.api.dsl.androidLibrary
import com.thomaskioko.tvmaniac.gradle.plugin.utils.addIfNotNull
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getDependencyOrNull
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getVersion
import com.thomaskioko.tvmaniac.gradle.plugin.utils.jvmCompilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class AndroidMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin("com.android.kotlin.multiplatform.library")) {
            target.plugins.apply("com.android.kotlin.multiplatform.library")
        }
        target.plugins.apply(BasePlugin::class.java)

        target.configureAndroidKotlinMultiplatform()
    }
}

@Suppress("UnstableApiUsage")
private fun Project.configureAndroidKotlinMultiplatform() {
    kotlinMultiplatform {
        androidLibrary {
            namespace = pathBasedAndroidNamespace()
            compileSdk = getVersion("android-compile").toInt()
            minSdk = getVersion("android-min").toInt()

            val desugarLibrary = project.getDependencyOrNull("android-desugarJdkLibs")
            project.dependencies.addIfNotNull("coreLibraryDesugaring", desugarLibrary)

            jvmCompilerOptions {
                enableCoreLibraryDesugaring = true
            }
        }
    }
}
