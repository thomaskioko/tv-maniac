plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.syncActivity.api)

                implementation(projects.data.database.sqldelight)
                implementation(libs.coroutines.core)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
