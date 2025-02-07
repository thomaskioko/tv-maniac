import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.DEFAULT
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

group = "com.thomaskioko.tvmaniac.plugins"

val jvmTargetVersion = libs.versions.jvmTarget.map(JvmTarget::fromTarget)

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

subprojects {
  pluginManager.withPlugin("java") {
    configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(
          JavaLanguageVersion.of(libs.versions.jdk.get().removeSuffix("-ea").toInt())
        )
      }
    }

    tasks.withType<JavaCompile>().configureEach {
      options.release.set(libs.versions.jvmTarget.map(String::toInt))
    }
  }

  plugins.withType<KotlinBasePlugin>().configureEach {
    tasks.withType<KotlinCompile>().configureEach {
      compilerOptions {
        val kotlinVersion = DEFAULT
        languageVersion.set(kotlinVersion)
        apiVersion.set(kotlinVersion)

        if (kotlinVersion != DEFAULT) {
          // Gradle/IntelliJ forces a lower version of kotlin, which results in warnings that
          // prevent use of this sometimes.
          // https://github.com/gradle/gradle/issues/16345
          allWarningsAsErrors.set(false)
        } else {
          allWarningsAsErrors.set(true)
        }

        this.jvmTarget.set(jvmTargetVersion)
        // https://jakewharton.com/kotlins-jdk-release-compatibility-flag/
        freeCompilerArgs.add(jvmTargetVersion.map { "-Xjdk-release=${it.target}" })
      }
    }
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
