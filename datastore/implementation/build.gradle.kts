plugins {
  alias(libs.plugins.tvmaniac.kotlin.android)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.datastore.api)

        api(libs.androidx.datastore.preference)

        implementation(libs.kotlinInject.runtime)
      }
    }

    commonTest { dependencies { implementation(libs.bundles.unittest) } }
  }
}

dependencies {
  add("kspAndroid", libs.kotlinInject.compiler)
  add("kspIosX64", libs.kotlinInject.compiler)
  add("kspIosArm64", libs.kotlinInject.compiler)
}

android { namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation" }
