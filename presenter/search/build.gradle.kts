import com.thomaskioko.tvmaniac.plugins.addLanguageArgs

plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.util)
        implementation(projects.data.featuredshows.api)
        implementation(projects.data.trendingshows.api)
        implementation(projects.data.upcomingshows.api)
        implementation(projects.data.search.api)
        implementation(projects.data.genre.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

        implementation(libs.coroutines.core)
        implementation(libs.bundles.kotlinInject)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.core.util.testing)
        implementation(projects.data.search.testing)
        implementation(projects.data.genre.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}

addLanguageArgs(
  "kotlinx.coroutines.ExperimentalCoroutinesApi",
)
