plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.syncActivity.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
