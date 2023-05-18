plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            api(libs.appauth)
            api(libs.coroutines.core)
        }

        sourceSets["commonMain"].dependencies {
            api(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth.api"
}
