plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:database"))
            implementation(project(":shared:data:trakt-profile:api"))
            implementation(libs.coroutines.core)
        }
    }
}


android {
    namespace = "com.thomaskioko.tvmaniac.trakt.profile.testing"
}