plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                api(projects.core.networkUtil)

                api(libs.coroutines.core)
            }
        }
    }
}
