plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(projects.data.database.sqldelight)
        }
        commonMain.dependencies {
            api(projects.data.watchStatus.api)
            api(libs.coroutines.core)
        }
    }
}
