package com.thomaskioko.tvmaniac.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermissions

class GitHooksPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.register("installGitHooks") {
      doLast {
        val hooksDir = project.rootProject.rootDir.resolve(".git/hooks")
        val hookFile = hooksDir.resolve("pre-commit")
        val scriptFile = project.rootProject.rootDir.resolve("scripts/pre-commit")

        hookFile.outputStream().use { output ->
          scriptFile.inputStream().use { it.copyTo(output) }
        }

        // Make the hook executable
        try {
          val permissions = PosixFilePermissions.fromString("rwxr-xr-x")
          Files.setPosixFilePermissions(Paths.get(hookFile.toURI()), permissions)
        } catch (e: UnsupportedOperationException) {
          // If POSIX permissions are not supported (e.g., on Windows), we'll use the following
          hookFile.setExecutable(true, false)
        }

        println("🧰 Git pre-commit hook installed successfully.")
      }
    }
  }
}
