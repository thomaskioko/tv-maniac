plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(projects.core.networkutil)
            api(projects.core.database)
            implementation(projects.data.shows.api)

            api(libs.coroutines.core)
            api(libs.kotlinx.atomicfu)
            api(libs.store5)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.similar.api"
}
