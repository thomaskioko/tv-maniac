plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {

        commonMain {
            dependencies {
                api(projects.core.networkutil)
                api(projects.core.database)
                implementation(projects.data.shows.api)

                api(libs.coroutines.core)
                api(libs.kotlinx.atomicfu)
                api(libs.store5)
            }
        }
    }
}

