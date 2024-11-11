plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.presenter.discover)
        implementation(projects.presenter.library)
        implementation(projects.presenter.search)
        implementation(projects.presenter.settings)
        implementation(projects.traktAuth.api)

        implementation(libs.decompose.decompose)
        implementation(libs.essenty.lifecycle)

        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.datastore.testing)
        implementation(projects.core.util.testing)
        implementation(projects.traktAuth.testing)
        implementation(projects.data.featuredshows.testing)
        implementation(projects.data.library.testing)
        implementation(projects.data.popularshows.testing)
        implementation(projects.data.search.testing)
        implementation(projects.data.topratedshows.testing)
        implementation(projects.data.trendingshows.testing)
        implementation(projects.data.upcomingshows.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
