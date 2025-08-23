package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

/**
 * Retrieves the version string associated with the given [name] from the project's properties.
 */
internal fun Project.getVersion(name: String): String {
    return getVersionOrNull(name) ?: throw NoSuchElementException("Could not find version $name")
}

/**
 * Retrieves the version of a library dependency specified by its name, or null if not found.
 */
internal fun Project.getVersionOrNull(name: String): String? {
    return libs.findVersion(name).orElseGet { null }?.requiredVersion
}


/**
 * Adds a dependency to the dependency handler if the provided dependency object is not null.
 */
internal fun DependencyHandler.addIfNotNull(name: String, dependency: Any?) {
    if (dependency != null) {
        add(name, dependency)
    }
}

/**
 * Retrieves a library dependency from the version catalog by its alias.
 */
internal fun Project.getDependency(name: String): Provider<MinimalExternalModuleDependency> {
    return libs.findLibrary(name).orElseThrow { NoSuchElementException("Could not find library $name") }
}

/**
 * Retrieves a library bundle from the version catalog by its alias.
 */
internal fun Project.getBundleDependencies(name: String): Provider<ExternalModuleDependencyBundle> {
    return libs.findBundle(name).orElseThrow { NoSuchElementException("Could not find library $name") }
}

/**
 * Retrieves a dependency bundle from the `libs` catalog by its name, or `null` if the bundle is not found.
 */
internal fun Project.getDependencyOrNull(name: String): Provider<MinimalExternalModuleDependency>? {
    return libs.findLibrary(name).orElseGet { null }
}

internal val Project.javaTarget: String
    get() = getVersion("java-target")

internal val Project.javaTargetProvider: Provider<String>
    get() = provider { getVersion("java-target") }

internal val Project.javaTargetVersion: Provider<JavaVersion>
    get() = javaTargetProvider.map { JavaVersion.toVersion(it) }

internal val Project.jvmTarget: Provider<JvmTarget>
    get() = javaTargetProvider.map { JvmTarget.fromTarget(it) }

internal val Project.javaToolchainVersion: Provider<JavaLanguageVersion>
    get() = provider { JavaLanguageVersion.of(getVersion("java-toolchain")) }

