plugins {
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.coroutines.core)

        implementation(projects.core.base)
        implementation(projects.core.database)
        implementation(projects.core.traktApi.api)
        implementation(projects.core.util)
        implementation(projects.data.library.api)

        implementation(libs.kotlinInject.runtime)
        implementation(libs.sqldelight.extensions)
      }
    }
  }
}

dependencies {
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
