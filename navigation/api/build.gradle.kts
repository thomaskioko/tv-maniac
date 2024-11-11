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
        implementation(projects.presenter.discover)
        implementation(projects.presenter.home)
        implementation(projects.presenter.library)
        implementation(projects.presenter.moreShows)
        implementation(projects.presenter.search)
        implementation(projects.presenter.seasondetails)
        implementation(projects.presenter.settings)
        implementation(projects.presenter.showDetails)
        implementation(projects.presenter.trailers)

        implementation(libs.coroutines.core)
      }
    }
  }
}
