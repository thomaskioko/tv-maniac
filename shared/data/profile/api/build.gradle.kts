plugins {
    id("tvmaniac.kmm.data")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:core:base"))
            api(project(":shared:core:networkutil"))
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.profile.api"
}