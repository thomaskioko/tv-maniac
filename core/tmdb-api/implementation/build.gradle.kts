plugins {
    id("plugin.tvmaniac.android.library")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.ktor.okhttp)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.core.tmdbApi.api)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.ktor.core)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.sqldelight.extensions)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.ktor.serialization)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.ktor.negotiation)
            }
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.implementation"
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}
