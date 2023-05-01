plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(projects.core.database)
            api(projects.core.networkutil)
            implementation(projects.core.tmdbApi.api)
            implementation(projects.data.shows.api)

            api(libs.coroutines.core)

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
