plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.rewatch.api)
                api(libs.coroutines.core)
            }
        }
    }
}
