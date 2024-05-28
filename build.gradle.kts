import com.diffplug.gradle.spotless.SpotlessExtension

buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.dependency.analysis) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.multiplatform) apply false
  alias(libs.plugins.serialization) apply false
  alias(libs.plugins.skie) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.sqldelight) apply false
}

allprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  configure<SpotlessExtension> {
    kotlin {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("src/**/*.kt")
      targetExclude("${layout.buildDirectory}/**/*.kt")
    }
    kotlinGradle {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("*.kts")
      targetExclude("${layout.buildDirectory}/**/*.kts")
    }
    format("xml") {
      target("src/**/*.xml")
      targetExclude("**/build/", ".idea/")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }

  afterEvaluate {
    // Remove log pollution until Android support in KMP improves.
    project.extensions
      .findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()
      ?.let { kmpExt ->
        kmpExt.sourceSets.removeAll {
          setOf(
              "androidAndroidTestRelease",
              "androidTestFixtures",
              "androidTestFixturesDebug",
              "androidTestFixturesRelease",
              "androidTestFixturesDemo"
            )
            .contains(it.name)
        }
      }
  }
}
