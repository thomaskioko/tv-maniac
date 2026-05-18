plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.watchedShows.api)
                api(libs.coroutines.core)
            }
        }
    }
}
