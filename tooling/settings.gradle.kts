@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
      google {
        content {
          includeGroupByRegex(".*google.*")
          includeGroupByRegex(".*android.*")
        }
      }
      mavenCentral()
      gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "tooling"

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("tvmaniac-gradle-plugins")
