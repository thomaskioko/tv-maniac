package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

internal fun Project.addImplementationDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "implementation",
        commonConfiguration = "commonMainImplementation",
        targetConfiguration = KotlinTarget::implementationConfigName,
        limitToTargets = limitToTargets,
    )
}

internal fun Project.addBundleImplementationDependency(
    dependency: Provider<ExternalModuleDependencyBundle>,
    limitToTargets: Set<KotlinPlatformType>? = null,
) {
    addBundleDependency(
        dependency = dependency,
        notMultiplatformConfiguration = "implementation",
        commonConfiguration = "commonMainImplementation",
        targetConfiguration = KotlinTarget::implementationConfigName,
        limitToTargets = limitToTargets,
    )
}

private fun <T> Project.addDependencyInternal(
    dependency: Provider<T>?,
    notMultiplatformConfiguration: String,
    commonConfiguration: String,
    targetConfiguration: KotlinTarget.() -> String,
    limitToTargets: Set<KotlinPlatformType>?,
) {
    if (dependency == null) return

    val extension = kotlinExtension
    if (extension is KotlinMultiplatformExtension) {
        if (limitToTargets == null) {
            dependencies.add(commonConfiguration, dependency)
        } else {
            extension.targets.configureEach {
                if (it.platformType in limitToTargets) {
                    dependencies.add(it.targetConfiguration(), dependency)
                }
            }
        }
    } else {
        dependencies.add(notMultiplatformConfiguration, dependency)
    }
}

private fun Project.addBundleDependency(
    dependency: Provider<ExternalModuleDependencyBundle>?,
    notMultiplatformConfiguration: String,
    commonConfiguration: String,
    targetConfiguration: KotlinTarget.() -> String,
    limitToTargets: Set<KotlinPlatformType>?,
) {
    addDependencyInternal(
        dependency,
        notMultiplatformConfiguration,
        commonConfiguration,
        targetConfiguration,
        limitToTargets,
    )
}

private fun Project.addDependency(
    dependency: Provider<MinimalExternalModuleDependency>?,
    notMultiplatformConfiguration: String,
    commonConfiguration: String,
    targetConfiguration: KotlinTarget.() -> String,
    limitToTargets: Set<KotlinPlatformType>?,
) {
    addDependencyInternal(
        dependency,
        notMultiplatformConfiguration,
        commonConfiguration,
        targetConfiguration,
        limitToTargets,
    )
}

internal fun Project.addKspDependencyForAllTargets(dependency: Provider<MinimalExternalModuleDependency>) =
    addKspDependencyForAllTargets("", dependency)

private fun Project.addKspDependencyForAllTargets(
    configurationNameSuffix: String,
    dependency: Provider<MinimalExternalModuleDependency>,
) {
    when {
        isKmpProject() -> addKspDependencyForKmp(configurationNameSuffix, dependency)
        else -> addKspDependencyForSinglePlatform(configurationNameSuffix, dependency)
    }
}

private fun Project.isKmpProject(): Boolean =
    extensions.findByType(KotlinMultiplatformExtension::class.java) != null

private fun Project.addKspDependencyForKmp(
    configurationNameSuffix: String,
    dependency: Provider<MinimalExternalModuleDependency>,
) {
    val kmpExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
    kmpExtension.targets.names
        .asSequence()
        .map { it.replaceFirstChar(Char::uppercaseChar) }
        .map { target -> if (target == "Metadata") "CommonMainMetadata" else target }
        .forEach { targetConfigSuffix ->
            dependencies.add("ksp$targetConfigSuffix$configurationNameSuffix", dependency)
        }
}

private fun Project.addKspDependencyForSinglePlatform(
    configurationNameSuffix: String,
    dependency: Provider<MinimalExternalModuleDependency>,
) {
    dependencies.add("ksp$configurationNameSuffix", dependency)
}

internal fun KotlinTarget.implementationConfigName(): String {
    return when (targetName) {
        "main" -> "implementation"
        else -> "${targetName}MainImplementation"
    }
}
