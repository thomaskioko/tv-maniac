plugins {
    id("tvmaniac.kmm.api")
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(libs.ktor.serialization)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.service.api"
}
