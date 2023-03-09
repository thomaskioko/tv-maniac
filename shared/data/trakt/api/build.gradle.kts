plugins {
    id("tvmaniac.kmm.api")
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(libs.inject)
            implementation(libs.appauth)
            implementation(libs.androidx.compose.activity)
            implementation(libs.androidx.core)
        }

        sourceSets["commonMain"].dependencies {
            implementation(libs.ktor.serialization)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trackt.auth.api"
}
