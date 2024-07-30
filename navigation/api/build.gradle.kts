plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)

        implementation(projects.datastore.api)
        implementation(projects.presentation.discover)
        implementation(projects.presentation.home)
        implementation(projects.presentation.library)
        implementation(projects.presentation.moreShows)
        implementation(projects.presentation.search)
        implementation(projects.presentation.seasondetails)
        implementation(projects.presentation.settings)
        implementation(projects.presentation.showDetails)
        implementation(projects.presentation.trailers)

        implementation(libs.coroutines.core)
      }
    }
  }
}
