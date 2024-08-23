plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.library.api)
        implementation(projects.data.seasons.api)
        implementation(projects.data.showdetails.api)
        implementation(projects.data.shows.api)
        implementation(projects.data.similar.api)
        implementation(projects.data.trailers.api)
        implementation(projects.data.cast.api)
        implementation(projects.data.recommendedshows.api)
        implementation(projects.data.watchproviders.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.cast.testing)
        implementation(projects.data.library.testing)
        implementation(projects.data.recommendedshows.testing)
        implementation(projects.data.seasons.testing)
        implementation(projects.data.showdetails.testing)
        implementation(projects.data.similar.testing)
        implementation(projects.data.trailers.testing)
        implementation(projects.data.watchproviders.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
