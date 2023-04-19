plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(projects.shared.core.database)
            implementation(projects.shared.data.profile.api)
            implementation(libs.coroutines.core)
        }
    }
}


android {
    namespace = "com.thomaskioko.tvmaniac.trakt.profile.testing"
}