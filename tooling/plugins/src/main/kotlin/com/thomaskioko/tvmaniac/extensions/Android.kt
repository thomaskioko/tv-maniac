package com.thomaskioko.tvmaniac.extensions

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
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
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
      isCoreLibraryDesugaringEnabled = true
    }

    packagingOptions {
      resources {
        excludes +=  "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
  }

  dependencies {
    add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
  }
}

fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure<BaseExtension>(action)

