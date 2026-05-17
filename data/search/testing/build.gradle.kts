plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(projects.data.shows.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.data.search.api)
                api(libs.coroutines.core)
            }
        }
    }
}
