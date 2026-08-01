plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.datastore.api)
                api(libs.coroutines.core)
            }
        }
    }
}
