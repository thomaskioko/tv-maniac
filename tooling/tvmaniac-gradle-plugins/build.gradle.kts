plugins {
  id("java-gradle-plugin")
  id("org.jetbrains.kotlin.jvm")
}

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
  }
}

kotlin {
  explicitApi()
}

dependencies {
  api(libs.kotlin.gradle.plugin)
  implementation(libs.android.gradle.plugin)
  implementation(libs.compose.compiler.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin.api)
  implementation(libs.dependency.analysis.gradle.plugin)

  compileOnly(libs.baselineprofile.gradlePlugin)
  compileOnly(libs.skie.gradle.plugin)
  compileOnly(libs.spotless.plugin)
  implementation(libs.gradle.doctor.gradle.plugin)
  runtimeOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
  plugins {
    create("appPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.app"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.AppPlugin"
    }

    create("androidPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.android"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.AndroidPlugin"
    }

    create("jvmPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.jvm"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.JvmPlugin"
    }

    create("baselineProfilePlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.baseline.profile"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.BaselineProfilePlugin"
    }

    create("commonMultiplatformPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.multiplatform"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.KotlinMultiplatformPlugin"
    }

    create("basePlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.base"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.BasePlugin"
    }

    create("rootPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.root"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.RootPlugin"
    }

    create("spotlessPlugin") {
      id = "com.thomaskioko.tvmaniac.gradle.spotless"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.checks.SpotlessPlugin"
    }

    create("xcframework") {
      id = "com.thomaskioko.tvmaniac.gradle.xcframework"
      implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.tasks.XCFrameworkPlugin"
    }
  }
}
