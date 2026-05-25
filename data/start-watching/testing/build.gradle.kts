plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.startWatching.api)
                api(libs.coroutines.core)
            }
        }
    }
}
