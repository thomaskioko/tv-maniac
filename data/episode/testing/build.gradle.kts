plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episode.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.coroutines.core)
            }
        }
    }
}
