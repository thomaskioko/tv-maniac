plugins {
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(projects.traktAuth.api)

      implementation(projects.presentation.discover)
      implementation(projects.presentation.library)
      implementation(projects.presentation.moreShows)
      implementation(projects.presentation.search)
      implementation(projects.presentation.seasondetails)
      implementation(projects.presentation.settings)
      implementation(projects.presentation.showDetails)
      implementation(projects.presentation.trailers)

      implementation(libs.kotlinInject.runtime)

      api(libs.decompose.decompose)
      api(libs.essenty.lifecycle)
    }

    commonTest.dependencies {
      implementation(projects.datastore.testing)
      implementation(projects.traktAuth.testing)
      implementation(projects.data.cast.testing)
      implementation(projects.data.featuredshows.testing)
      implementation(projects.data.library.testing)
      implementation(projects.data.popularshows.testing)
      implementation(projects.data.recommendedshows.testing)
      implementation(projects.data.seasons.testing)
      implementation(projects.data.seasondetails.testing)
      implementation(projects.data.showdetails.testing)
      implementation(projects.data.similar.testing)
      implementation(projects.data.topratedshows.testing)
      implementation(projects.data.trailers.testing)
      implementation(projects.data.trendingshows.testing)
      implementation(projects.data.upcomingshows.testing)
      implementation(projects.data.watchproviders.testing)

      implementation(libs.bundles.unittest)
    }
  }
}
