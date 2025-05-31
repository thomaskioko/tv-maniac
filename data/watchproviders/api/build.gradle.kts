plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil)
                api(projects.data.shows.api)

                implementation(projects.data.database.sqldelight)

                api(libs.coroutines.core)
            }
        }
    }
}
