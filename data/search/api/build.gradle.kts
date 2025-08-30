plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)
                implementation(projects.core.networkUtil)

                api(libs.coroutines.core)
            }
        }
    }
}
