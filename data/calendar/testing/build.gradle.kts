plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.calendar.api)
                api(libs.coroutines.core)
            }
        }
    }
}
