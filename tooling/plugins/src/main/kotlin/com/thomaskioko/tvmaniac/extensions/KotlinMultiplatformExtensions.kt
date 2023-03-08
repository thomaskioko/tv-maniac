package com.thomaskioko.tvmaniac.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
internal fun Project.configureKotlinMultiplatform(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 33

        defaultConfig {
            minSdk = 23
            manifestPlaceholders["appAuthRedirectScheme"] = "empty"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

