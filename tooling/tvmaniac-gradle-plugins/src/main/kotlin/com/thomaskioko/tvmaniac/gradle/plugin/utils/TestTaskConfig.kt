package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.tasks.testing.Test

internal fun Test.defaultTestSetup() {
    val projectNameProvider = project.provider {
        project.path
            .replace("projects", "")
            .replaceFirst(":", "")
            .replace(":", "/")
    }

    reports.html.outputLocation.set(
        project.rootProject.layout.buildDirectory.dir(
            projectNameProvider.map { "reports/tests/$it" }
        )
    )
    reports.junitXml.outputLocation.set(
        project.rootProject.layout.buildDirectory.dir(
            projectNameProvider.map { "reports/tests/$it" }
        )
    )

    maxParallelForks = project.providers.systemProperty("test.maxParallelForks")
        .map { it.toIntOrNull() ?: (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1) }
        .orElse((Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1))
        .get()
}
