package com.thomaskioko.tvmaniac.gradle.plugin.extensions

import com.thomaskioko.tvmaniac.gradle.plugin.utils.addBundleImplementationDependency
import com.thomaskioko.tvmaniac.gradle.plugin.utils.addImplementationDependency
import com.thomaskioko.tvmaniac.gradle.plugin.utils.addKspDependencyForAllTargets
import com.thomaskioko.tvmaniac.gradle.plugin.utils.compilerOptions
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getBundleDependencies
import com.thomaskioko.tvmaniac.gradle.plugin.utils.getDependency
import com.thomaskioko.tvmaniac.gradle.plugin.utils.kotlin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

public abstract class BaseExtension(private val project: Project) : ExtensionAware {
    public fun explicitApi() {
        project.kotlin {
            explicitApi()
        }
    }

    public fun optIn(vararg classes: String) {
        project.kotlin {
            compilerOptions {
                optIn.addAll(*classes)
            }
        }
    }

    public fun useSerialization() {
        project.plugins.apply("org.jetbrains.kotlin.plugin.serialization")

        project.addImplementationDependency(project.getDependency("kotlin-serialization-core"))
    }

    public fun useKspAnvil() {
        project.plugins.apply("com.google.devtools.ksp")

        project.addKspDependencyForAllTargets(project.getDependency("kotlinInject-compiler"))
        project.addKspDependencyForAllTargets(project.getDependency("kotlinInject-anvil-compiler"))

    }

    public fun useKotlinInjectAnvilCompiler() {
        project.plugins.apply("com.google.devtools.ksp")

        project.addBundleImplementationDependency(project.getBundleDependencies("kotlinInject"))

        project.addKspDependencyForAllTargets(project.getDependency("kotlinInject-anvil-compiler"))
    }

    public fun android(configure: AndroidExtension.() -> Unit) {
        val androidExtension = extensions.findByType(AndroidExtension::class.java)
            ?: throw IllegalStateException("Android extension not found. Did you call addAndroidTarget()?")
        androidExtension.configure()
    }
}
