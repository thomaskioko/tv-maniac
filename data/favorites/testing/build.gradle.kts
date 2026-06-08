plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.favorites.api)
                api(libs.coroutines.core)
            }
        }
    }
}
