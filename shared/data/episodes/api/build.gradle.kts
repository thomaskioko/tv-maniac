plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:util"))
            api(project(":shared:networkutil"))
            api(project(":shared:data:database"))

            api(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodes.api"
}