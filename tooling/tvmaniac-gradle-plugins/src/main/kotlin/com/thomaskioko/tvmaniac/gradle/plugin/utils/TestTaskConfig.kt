package com.thomaskioko.tvmaniac.gradle.plugin.utils

import org.gradle.api.tasks.testing.Test
import kotlin.text.replace
import kotlin.text.replaceFirst

internal fun Test.defaultTestSetup() {
    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")
    reports.html.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
    reports.junitXml.outputLocation.set(project.rootProject.layout.buildDirectory.dir("reports/tests/$projectName"))
}
