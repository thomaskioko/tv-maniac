package com.thomaskioko.tvmaniac.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import com.thomaskioko.tvmaniac.extensions.Versions
import com.thomaskioko.tvmaniac.extensions.configureAndroid
import com.thomaskioko.tvmaniac.extensions.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class ApplicationPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        apply("com.autonomousapps.dependency-analysis")
        apply("androidx.baselineprofile")
        apply("com.google.devtools.ksp")
      }

      extensions.configure<ApplicationExtension> {
        defaultConfig {
          applicationId = "com.thomaskioko.tvmaniac"
          versionCode = 1
          versionName = "1.0"
          targetSdk = Versions.TARGET_SDK

          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildFeatures.buildConfig = true

        configureAndroid()
        configureAndroidCompose(this)

        buildTypes {
          debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
          }

          release {
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs["debug"]
            isShrinkResources = true
            isMinifyEnabled = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
          }
        }

        @Suppress("UnstableApiUsage")
        testOptions {
          managedDevices {
            devices.create<ManagedVirtualDevice>("pixel6Api34") {
              device = "Pixel 6"
              apiLevel = 34
              systemImageSource = "aosp"
            }
          }
        }
      }
    }
  }
}
