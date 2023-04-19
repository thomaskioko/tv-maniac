plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.datastore.api)
            implementation(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.datastore.testing"
}
