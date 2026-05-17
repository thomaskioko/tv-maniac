plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.cast.api)
                api(projects.data.database.sqldelight)
                api(libs.coroutines.core)
            }
        }
    }
}
