plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {

        commonMain {
            dependencies {
                api(projects.core.database)
                api(projects.core.util)
                implementation(projects.core.tmdbApi.api)

                api(libs.coroutines.core)

            }
        }
    }
}
