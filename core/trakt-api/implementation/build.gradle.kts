plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(libs.appauth)
            implementation(libs.ktor.okhttp)
        }


        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.core.traktApi.api)
            implementation(projects.core.datastore.api)

            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.serialization)
            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["iosMain"].dependencies {
            implementation(projects.core.traktApi.api)

            implementation(libs.ktor.logging)
            implementation(libs.ktor.darwin)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.trakt.api.implementation"
}