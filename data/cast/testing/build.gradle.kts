plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.cast.api)
                implementation(projects.data.database.sqldelight)

                implementation(libs.coroutines.core)
            }
        }
    }
}
