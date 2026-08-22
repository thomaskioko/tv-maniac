plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.backup.api)
                api(libs.coroutines.core)
            }
        }
    }
}
