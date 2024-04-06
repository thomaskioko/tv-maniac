package com.thomaskioko.tvmaniac.plugins

import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.Versions
import com.thomaskioko.tvmaniac.extensions.configureAndroid
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import com.thomaskioko.tvmaniac.extensions.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {

                defaultConfig.targetSdk = Versions.TARGET_SDK

              configureKotlinJvm()
                configureAndroid()
                configureFlavors(this)
            }
        }
    }
}
