package com.thomaskioko.tvmaniac.gradle.plugin.utils

import com.android.build.api.dsl.Lint
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal fun Project.configureStandaloneLint() {
    extensions.configure(Lint::class.java) {
        it.configure(project)
    }
}

internal fun Lint.configure(project: Project) {
    lintConfig = project.rootProject.file("gradle/lint.xml")

    checkReleaseBuilds = false
    checkGeneratedSources = false
    checkTestSources = false
    checkDependencies = true
    ignoreTestSources = true
    abortOnError = true
    warningsAsErrors = true

    // Temporary workaround: Compose lint detector ComposableFlowOperatorDetector crashes with Kotlin metadata 2.1.0
    // Disable the specific check until Compose lint supports metadata 2.1.0
    // See build failure message suggesting: disable "FlowOperatorInvokedInComposition"
    disable.addAll(
        listOf(
            "FlowOperatorInvokedInComposition",
            "StateFlowValueCalledInComposition",
        ),
    )

    htmlReport = true
    htmlOutput = project.reportsFile("lint-result.html").get().asFile
    textReport = true
    textOutput = project.reportsFile("lint-result.txt").get().asFile

    project.dependencies.addIfNotNull("lintChecks", project.getBundleDependencies("lint"))
}

private fun Project.reportsFile(name: String): Provider<RegularFile> {
    val projectName = project.path
        .replace("projects", "")
        .replaceFirst(":", "")
        .replace(":", "/")

    return rootProject.layout.buildDirectory.file("reports/lint/$projectName/$name")
}
