plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }

  optIn(
    "androidx.paging.ExperimentalPagingApi",
  )
}


kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger.api)
        implementation(projects.core.paging)
        implementation(projects.core.networkUtil)
        implementation(projects.core.util)
        implementation(projects.data.popularshows.api)
        implementation(projects.data.requestManager.api)
        implementation(projects.database)
        implementation(projects.tmdbApi.api)

        api(libs.coroutines.core)

        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}
