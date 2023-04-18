plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.database)
            implementation(projects.shared.core.util)
            implementation(projects.shared.data.shows.api)
            implementation(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shows.testing"
}
