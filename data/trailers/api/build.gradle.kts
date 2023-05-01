plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(projects.core.database)
            api(projects.core.networkutil)

            api(libs.coroutines.core)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trailers.api"
}
