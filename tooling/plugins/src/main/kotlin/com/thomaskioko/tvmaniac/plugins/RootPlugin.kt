package com.thomaskioko.tvmaniac.plugins

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = with(target) {
    require(project == project.rootProject) {
      "Root plugin should only be applied on the root project."
    }

    pluginManager.withPlugin("com.autonomousapps.dependency-analysis") {
      configureDependencyAnalysis()
    }
  }
}

fun Project.configureDependencyAnalysis() {
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

    structure {
      bundle("androidx-compose-foundation") {
        primary("androidx.compose.foundation:foundation")
        includeGroup("androidx.compose.animation")
        includeGroup("androidx.compose.foundation")
      }

      bundle("androidx-compose-ui") {
        primary("androidx.compose.ui:ui")
        includeGroup("androidx.compose.ui")
      }

      bundle("androidx-compose-material") {
        primary("androidx.compose.material:material")
        includeGroup("androidx.compose.material")
      }

      bundle("androidx-activity") {
        include("^androidx.activity:activity.*")
      }

      bundle("coil") {
        includeDependency("io.coil-kt:coil")
        includeDependency("io.coil-kt:coil-base")
      }

      bundle("coil-compose") {
        primary("io.coil-kt:coil-compose")
        includeDependency("io.coil-kt:coil-compose")
        includeDependency("io.coil-kt:coil-compose-base")
      }
    }
  }
}


