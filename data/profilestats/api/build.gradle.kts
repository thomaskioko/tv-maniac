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
            api(projects.core.util)

            api(libs.coroutines.core)
            api(libs.kotlinx.atomicfu)
            api(libs.store5)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.profile.api"
}