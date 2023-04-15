plugins {
    id("tvmaniac.kmm.data")
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:core:networkutil"))
            implementation(libs.ktor.serialization)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.api.api"
}
