plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil.api)

                api(projects.data.database.sqldelight)
                implementation(projects.data.shows.api)

                api(libs.coroutines.core)
            }
        }
    }
}
