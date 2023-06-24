plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
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
                implementation(projects.data.requestManager.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.resourcemanager.implementation"
}