plugins {
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.database)
        implementation(projects.tmdbApi.api)
        implementation(projects.core.paging)
        implementation(projects.core.util)
        implementation(projects.data.featuredshows.api)
        implementation(projects.data.requestManager.api)
        implementation(projects.data.trendingshows.api)

        api(libs.coroutines.core)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.sqldelight.extensions)
        implementation(libs.store5)
      }
    }
  }
}

dependencies {
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
