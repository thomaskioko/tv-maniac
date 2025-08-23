package com.thomaskioko.tvmaniac.gradle.plugin.utils

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.AndroidComponentsExtension
import com.thomaskioko.tvmaniac.gradle.plugin.extensions.AndroidExtension
import com.thomaskioko.tvmaniac.gradle.plugin.extensions.BaseExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal val Project.baseExtension: BaseExtension
    get() = extensions.getByType(BaseExtension::class.java)

internal val Project.androidExtension: AndroidExtension
    get() = baseExtension.extensions.getByType(AndroidExtension::class.java)


/**
 * This function provides a convenient way to configure the Kotlin Multiplatform plugin using a lambda expression.
 */
internal fun Project.kotlinMultiplatform(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        it.block()
    }
}

/**
 * This function provides a convenient way to access and modify the settings of the Compose Compiler
 * plugin within a Gradle project.
 */
internal fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure(ComposeCompilerGradlePluginExtension::class.java) {
        it.block()
    }
}

internal fun Project.android(block: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    extensions.configure(CommonExtension::class.java) {
        it.block()
    }
}


/**
 * This function simplifies the process of configuring an Android application module by providing a type-safe builder pattern.
 */
internal fun Project.androidApp(block: ApplicationExtension.() -> Unit) {
    extensions.configure(ApplicationExtension::class.java) {
        it.block()
    }
}

/**
 * This function provides a concise way to customize the Android build process, including variant configuration and artifact management.
 */
internal fun Project.androidComponents(block: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    extensions.configure(AndroidComponentsExtension::class.java) {
        it.block()
    }
}

internal fun Project.kotlin(block: KotlinProjectExtension.() -> Unit) {
    (project.extensions.getByName("kotlin") as KotlinProjectExtension).block()
}

internal fun Project.java(block: JavaPluginExtension.() -> Unit) {
    extensions.configure(JavaPluginExtension::class.java) {
        it.block()
    }
}


internal fun KotlinProjectExtension.compilerOptions(configure: KotlinCommonCompilerOptions.() -> Unit) {
    when (this) {
        is KotlinJvmProjectExtension -> compilerOptions(configure)
        is KotlinAndroidProjectExtension -> compilerOptions(configure)
        is KotlinMultiplatformExtension -> {
            compilerOptions(configure)
            targets.configureEach { target ->
                (target as? HasConfigurableKotlinCompilerOptions<*>)?.compilerOptions(configure)
            }
        }

        else -> throw IllegalStateException("Unsupported kotlin extension ${this::class}")
    }
}

internal fun KotlinMultiplatformAndroidLibraryTarget.jvmCompilerOptions(block: KotlinJvmCompilerOptions.() -> Unit) {
    compilations.configureEach { compilation ->
        compilation.compilerOptions.configure {
            block()
        }
    }
}
