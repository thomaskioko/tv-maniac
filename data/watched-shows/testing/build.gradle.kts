plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.watchedShows.api)
                api(projects.data.watchedShows.implementation)
                api(libs.coroutines.core)
            }
        }
    }
}
