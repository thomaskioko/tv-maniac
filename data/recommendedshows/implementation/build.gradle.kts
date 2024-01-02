plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.util)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.recommendedshows.api)
                implementation(projects.data.shows.api)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.turbine)
                implementation(libs.kotest.assertions)
                implementation(libs.coroutines.test)
            }
        }
    }
}

