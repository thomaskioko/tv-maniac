plugins {
    id("tvmaniac.kmm.data")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:core:networkutil"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trailers.api"
}
