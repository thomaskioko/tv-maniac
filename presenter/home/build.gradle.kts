plugins {
  alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
  multiplatform {
    useKotlinInject()
    useKspAnvilCompiler()
    useSerialization()
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.presenter.discover)
        implementation(projects.presenter.search)
        implementation(projects.presenter.settings)
        implementation(projects.presenter.watchlist)
        implementation(projects.traktAuth.api)

        implementation(libs.decompose.decompose)
        implementation(libs.essenty.lifecycle)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.datastore.testing)
        implementation(projects.core.util.testing)
        implementation(projects.traktAuth.testing)
        implementation(projects.data.featuredshows.testing)
        implementation(projects.data.genre.testing)
        implementation(projects.data.watchlist.testing)
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
