plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.episodes.api)
        implementation(projects.data.shows.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.sqldelight.extensions)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}
