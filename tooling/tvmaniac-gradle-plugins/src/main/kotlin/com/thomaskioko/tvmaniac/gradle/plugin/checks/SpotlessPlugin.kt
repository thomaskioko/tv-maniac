package com.thomaskioko.tvmaniac.gradle.plugin.checks

import com.diffplug.gradle.spotless.SpotlessExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpotlessPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply("com.diffplug.spotless")

        val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
        extensions.configure(SpotlessExtension::class.java) { extension ->
            with(extension) {
                kotlin {
                    it.ktlint(ktlintVersion).editorConfigOverride(
                        mapOf(
                            "android" to "true",
                        ),
                    )
                    it.target("**/*.kt")
                    it.targetExclude("${layout.buildDirectory}/**/*.kt")
                }

                kotlinGradle {
                    it.ktlint(ktlintVersion)
                    it.target("*.kts")
                }

                format("xml") {
                    it.target("src/**/*.xml")
                    it.targetExclude("**/build/", ".idea/")
                    it.trimTrailingWhitespace()
                    it.endWithNewline()
                }
            }
        }
    }
}
