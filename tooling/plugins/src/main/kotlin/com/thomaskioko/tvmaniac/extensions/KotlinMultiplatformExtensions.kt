package com.thomaskioko.tvmaniac.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

@Suppress("UnstableApiUsage")
internal fun Project.configureKotlinMultiplatform(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val minSdkSdkVersion = libs.findVersion("android-minSdk").get().toString().toInt()
        val sdkVersion = libs.findVersion("android-compileSdk")
            .get().toString().toInt()

        defaultConfig {
            minSdk = minSdkSdkVersion
            compileSdk = sdkVersion
            manifestPlaceholders["appAuthRedirectScheme"] = "empty"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

