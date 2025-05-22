plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
  }

  optIn(
    "kotlinx.coroutines.DelicateCoroutinesApi",
  )
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.coroutines.core)

        implementation(projects.api.tmdb.api)
        implementation(projects.core.base)
        implementation(projects.core.logger.api)
        implementation(projects.core.networkUtil)
        implementation(projects.core.util)
        implementation(projects.data.database.sqldelight)
        implementation(projects.domain.genre)

        implementation(libs.sqldelight.extensions)
        implementation(libs.store5)
      }
    }
  }
}
