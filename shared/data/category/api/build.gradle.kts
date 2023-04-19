plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(projects.shared.core.database)
            api(projects.shared.core.networkutil)
            implementation(projects.shared.core.tmdbApi.api)
            implementation(projects.shared.data.shows.api)

            api(libs.coroutines.core)

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
