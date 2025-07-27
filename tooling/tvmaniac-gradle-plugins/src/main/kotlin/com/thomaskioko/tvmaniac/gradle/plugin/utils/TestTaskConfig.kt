package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.tasks.testing.Test

internal fun Test.defaultTestSetup() {
    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")
    reports.html.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
    reports.junitXml.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}
