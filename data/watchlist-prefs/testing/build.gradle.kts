plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.watchlistPrefs.api)
                api(libs.coroutines.core)
            }
        }
    }
}
