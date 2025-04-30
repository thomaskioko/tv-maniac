package com.thomaskioko.tvmaniac.gradle.plugin.extensions

import com.thomaskioko.tvmaniac.gradle.plugin.utils.configureStandaloneLint
import org.gradle.api.Project

public abstract class JvmExtension(private val project: Project) {
  public fun useAndroidLint() {
    project.plugins.apply("com.android.lint")

    project.configureStandaloneLint()
  }
}
