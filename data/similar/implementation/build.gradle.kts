plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.core.util)
        implementation(projects.data.requestManager.api)
        implementation(projects.data.shows.api)
        implementation(projects.data.similar.api)
        implementation(projects.tmdbApi.api)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
        implementation(libs.kotlinx.atomicfu)
        implementation(libs.store5)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

dependencies {
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
