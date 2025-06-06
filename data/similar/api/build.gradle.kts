plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil)

                api(projects.data.database.sqldelight)
                implementation(projects.data.shows.api)

                api(libs.coroutines.core)
            }
        }
    }
}
