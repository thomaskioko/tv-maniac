plugins {
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(projects.traktAuth.api)
      implementation(projects.navigation.api)

      implementation(projects.presenter.discover)
      implementation(projects.presenter.home)
      implementation(projects.presenter.library)
      implementation(projects.presenter.moreShows)
      implementation(projects.presenter.search)
      implementation(projects.presenter.seasondetails)
      implementation(projects.presenter.settings)
      implementation(projects.presenter.showDetails)
      implementation(projects.presenter.trailers)

      implementation(libs.kotlinInject.runtime)
    }

    commonTest.dependencies {
      implementation(projects.core.util.testing)
      implementation(projects.datastore.testing)
      implementation(projects.traktAuth.testing)
      implementation(projects.data.cast.testing)
      implementation(projects.data.featuredshows.testing)
      implementation(projects.data.library.testing)
      implementation(projects.data.popularshows.testing)
      implementation(projects.data.recommendedshows.testing)
      implementation(projects.data.seasons.testing)
      implementation(projects.data.search.testing)
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
