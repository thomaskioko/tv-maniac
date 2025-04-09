plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.core.networkUtil)
        api(projects.data.shows.api)
        api(projects.database)

        implementation(projects.core.base)

        api(libs.coroutines.core)

        implementation(libs.kotlinInject.runtime)
      }
    }
  }
}
