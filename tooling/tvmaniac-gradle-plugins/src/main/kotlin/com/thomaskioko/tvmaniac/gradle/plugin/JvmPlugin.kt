package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.plugin.extensions.JvmExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.baseExtension
import com.thomaskioko.tvmaniac.gradle.plugin.utils.defaultTestSetup
import com.thomaskioko.tvmaniac.gradle.plugin.utils.java
import com.thomaskioko.tvmaniac.gradle.plugin.utils.javaTargetVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

/**
 * `JvmPlugin` is an abstract plugin that provides common configurations for JVM-based projects.
 */
public abstract class JvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply(BasePlugin::class.java)

        target.baseExtension.extensions.create("jvm", JvmExtension::class.java)

        target.java {
            sourceCompatibility = target.javaTargetVersion
            targetCompatibility = target.javaTargetVersion
        }

        target.tasks.withType(JavaCompile::class.java).configureEach {
            it.options.release.set(target.javaTargetVersion.majorVersion.toInt())
        }

        target.tasks.withType(Test::class.java).configureEach(Test::defaultTestSetup)
    }
}
