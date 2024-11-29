plugins {
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.base)
      implementation(libs.kermit)
      implementation(libs.napier)
      implementation(libs.bundles.kotlinInject)
    }
  }
}
