plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.database)
        implementation(projects.tmdbApi.api)
        implementation(projects.data.episodes.api)
        implementation(projects.data.requestManager.api)
        implementation(projects.data.seasons.api)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }
  }
}

dependencies {
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
