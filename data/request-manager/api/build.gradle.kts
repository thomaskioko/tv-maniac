plugins {
    id("tvmaniac.kmm.library")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android()
    listOf(
        iosX64(),
        iosArm64(),
    )

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.core.database)
                api(projects.core.util)
                api(libs.kotlinx.datetime)
            }
        }
    }
}


android {
    namespace = "com.thomaskioko.tvmaniac.resourcemanager.api"
}