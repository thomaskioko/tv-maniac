plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.episodes.api)
        implementation(projects.data.recommendedshows.api)
        implementation(projects.data.seasondetails.api)
        implementation(projects.data.cast.api)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
        api(libs.kotlinx.collections)

        implementation(libs.bundles.kotlinInject)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.seasondetails.testing)
        implementation(projects.data.cast.testing)

        implementation(libs.bundles.unittest)
      }
    }
  }
}
