import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.androidx.baselineprofile)
  alias(libs.plugins.tvmaniac.kotlin.android)
}

android {
  namespace = "com.thomaskioko.tvmaniac.benchmark"
  compileSdk = 35

  defaultConfig {
    minSdk = 28
    targetSdk = 35

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  @Suppress("UnstableApiUsage")
  testOptions {
    managedDevices {
      devices {
        create<ManagedVirtualDevice>("pixel6Api34") {
          device = "Pixel 6"
          apiLevel = 34
          systemImageSource = "aosp"
        }
      }
    }
  }

  targetProjectPath = ":app"
}

@Suppress("UnstableApiUsage")
baselineProfile {
  managedDevices += "pixel6Api34"
  useConnectedDevices = false

  // Set this to true for debugging
  enableEmulatorDisplay = false
}

dependencies {
  implementation(libs.androidx.junit)
  implementation(libs.androidx.espresso.core)
  implementation(libs.androidx.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
}

@Suppress("UnstableApiUsage")
androidComponents {
  onVariants { v ->
    val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
    v.instrumentationRunnerArguments.put(
      "targetAppId",
      v.testedApks.map { artifactsLoader.load(it)?.applicationId },
    )
  }
}
