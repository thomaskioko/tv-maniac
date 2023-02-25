plugins {
    id("tvmaniac.kmm.library")
}



kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:tmdb:api"))
            implementation(project(":shared:data:database"))
            implementation(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.testing"
}