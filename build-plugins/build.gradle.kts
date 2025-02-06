import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

group = "com.thomaskioko.tvmaniac.plugins"

val javaTarget: String = libs.versions.java.target.get()

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(javaTarget)
  }
}

tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = javaTarget
  targetCompatibility = javaTarget
}

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.compose.compiler.gradlePlugin)
  compileOnly(libs.dependency.analysis.gradlePlugin)
  compileOnly(libs.gradledoctor.plugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.spotless.plugin)
}

gradlePlugin {
  plugins {
    register("androidApplication") {
      id = "plugin.tvmaniac.application"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.ApplicationPlugin"
    }

    register("androidComposeLibrary") {
      id = "plugin.tvmaniac.compose.library"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.ComposeLibraryPlugin"
    }

    register("androidLibrary") {
      id = "plugin.tvmaniac.android.library"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.AndroidLibraryPlugin"
    }

    register("kotlinAndroid") {
      id = "plugin.tvmaniac.kotlin.android"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.KotlinAndroidPlugin"
    }

    register("git-hooks") {
      id = "plugin.tvmaniac.git-hooks"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.GitHooksPlugin"
    }

    register("kotlinMultiplatformPlugin") {
      id = "plugin.tvmaniac.multiplatform"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.KotlinMultiplatformPlugin"
    }

    register("root") {
      id = "plugin.tvmaniac.root"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.RootPlugin"
    }

    register("spotless") {
      id = "plugin.tvmaniac.spotless"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.SpotlessPlugin"
    }

    register("xcframework") {
      id = "plugin.tvmaniac.xcframework"
      implementationClass = "com.thomaskioko.tvmaniac.plugins.XCFrameworkPlugin"
    }
  }
}
