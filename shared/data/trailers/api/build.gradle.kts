plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trailers.api"
}
