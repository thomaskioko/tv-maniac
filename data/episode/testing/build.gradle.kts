plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.episode.api)
                implementation(projects.data.database.sqldelight)

                implementation(libs.kotlinx.datetime)
                implementation(libs.coroutines.core)
            }
        }
    }
}
