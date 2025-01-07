import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets

plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)

        implementation(projects.core.base)
        implementation(projects.datastore.api)
        implementation(projects.presenter.discover)
        implementation(projects.presenter.home)
        implementation(projects.presenter.watchlist)
        implementation(projects.presenter.moreShows)
        implementation(projects.presenter.search)
        implementation(projects.presenter.seasondetails)
        implementation(projects.presenter.settings)
        implementation(projects.presenter.showDetails)
        implementation(projects.presenter.trailers)

        implementation(libs.bundles.kotlinInject)
        implementation(libs.coroutines.core)
      }
    }
  }
}

addKspDependencyForAllTargets(libs.kotlinInject.anvil.compiler)
