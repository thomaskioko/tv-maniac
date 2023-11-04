package com.thomaskioko.tvmaniac.extensions

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.configureAndroid() {
  android {
    compileSdkVersion(Versions.COMPILE_SDK)

    defaultConfig {
      minSdk = Versions.MIN_SDK
      targetSdk = Versions.TARGET_SDK
      manifestPlaceholders["appAuthRedirectScheme"] = "empty"
    }

    compileOptions {
      isCoreLibraryDesugaringEnabled = true
    }

  }

  dependencies {
    "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())
  }
}

fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure<BaseExtension>(action)

