plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.connectedAccount.api)
                api(libs.coroutines.core)
            }
        }
    }
}
