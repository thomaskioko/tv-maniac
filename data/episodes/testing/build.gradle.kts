plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episodes.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
