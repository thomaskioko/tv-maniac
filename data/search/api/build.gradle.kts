plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.shows.api)
                api(libs.coroutines.core)
            }
        }
    }
}
