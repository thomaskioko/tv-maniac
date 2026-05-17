plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.seasons.api)
                api(projects.data.database.sqldelight)
                api(libs.coroutines.core)
            }
        }
    }
}
