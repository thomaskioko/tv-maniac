package com.thomaskioko.tvmaniac.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


internal fun Project.configureAndroidCompose(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
  commonExtension.apply {

    defaultConfig {
      minSdk = Versions.MIN_SDK
    }

    buildFeatures {
      compose = true
    }

    composeOptions {
      kotlinCompilerExtensionVersion = libs.findVersion("composecompiler").get().toString()
    }

    compileOptions {
      isCoreLibraryDesugaringEnabled = true
    }

    testOptions {
      unitTests {
        // For Robolectric
        isIncludeAndroidResources = true
      }
    }

    configureKotlinJvm()

    dependencies {
      val bom = libs.findLibrary("androidx-compose-bom").get()

      add("implementation", platform(bom))
      add("lintChecks", libs.findLibrary("lint-compose").get())
      add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
    }
  }
}
