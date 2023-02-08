plugins {
    id("tvmaniac.kmm.library")
}



kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:domain:tmdb:api"))
            implementation(project(":shared:core:database"))
            implementation(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.testing"
}