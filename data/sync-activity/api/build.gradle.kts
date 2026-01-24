plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)

                api(libs.coroutines.core)
                api(libs.kotlinx.datetime)
            }
        }
    }
}
