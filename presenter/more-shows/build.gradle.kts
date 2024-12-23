plugins { id("plugin.tvmaniac.multiplatform") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.popularshows.api)
        implementation(projects.data.topratedshows.api)
        implementation(projects.data.trendingshows.api)
        implementation(projects.data.upcomingshows.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

        implementation(libs.bundles.kotlinInject)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}
