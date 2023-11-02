package com.thomaskioko.tvmaniac.plugins

import com.thomaskioko.tvmaniac.extensions.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            configureAndroid()
        }
    }
}