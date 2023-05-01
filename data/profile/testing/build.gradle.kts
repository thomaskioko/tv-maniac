plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(projects.core.database)
            implementation(projects.data.profile.api)
            implementation(libs.coroutines.core)
        }
    }
}


android {
    namespace = "com.thomaskioko.tvmaniac.trakt.profile.testing"
}