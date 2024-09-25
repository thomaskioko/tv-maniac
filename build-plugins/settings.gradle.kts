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

rootProject.name = "build-plugins"
