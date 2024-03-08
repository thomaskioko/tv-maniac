plugins {
  id("plugin.tvmaniac.kotlin.android")
  id("plugin.tvmaniac.multiplatform")
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(projects.traktAuth.api)

        implementation(libs.androidx.activity)
        implementation(libs.androidx.browser)
        implementation(libs.androidx.core)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.logger)
        implementation(projects.traktAuth.api)
        implementation(libs.kotlinInject.runtime)
      }
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.traktauth.implementation" }

dependencies {
  add("kspAndroid", libs.kotlinInject.compiler)
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}
