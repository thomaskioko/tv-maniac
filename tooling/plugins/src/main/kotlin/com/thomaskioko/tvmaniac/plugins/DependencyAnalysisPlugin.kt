package com.thomaskioko.tvmaniac.plugins

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class DependencyAnalysisPlugin : Plugin<Project> {
  override fun apply(project: Project) = with(project) {
    plugins.apply("com.autonomousapps.dependency-analysis")

    configure<DependencyAnalysisExtension> {
      issues {
        all {
          onUnusedDependencies {
            severity("fail")
          }
          onRedundantPlugins {
            severity("fail")
          }
        }
      }
    }
  }
}
