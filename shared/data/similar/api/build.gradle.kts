plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(projects.shared.core.networkutil)
            api(projects.shared.core.database)
            implementation(projects.shared.data.shows.api)

            api(libs.coroutines.core)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.similar.api"
}
