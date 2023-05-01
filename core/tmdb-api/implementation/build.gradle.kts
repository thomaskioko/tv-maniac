plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            implementation(libs.ktor.okhttp)
        }

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.core.tmdbApi.api)
            implementation(projects.data.shows.api)

            implementation(libs.kotlinInject.runtime)
            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.sqldelight.extensions)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(libs.ktor.serialization)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)
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