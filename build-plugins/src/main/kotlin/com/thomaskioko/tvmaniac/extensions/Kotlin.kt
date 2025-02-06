package com.thomaskioko.tvmaniac.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinJvm() {
  tasks.withType(KotlinCompile::class.java).configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  extensions.configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }
}
