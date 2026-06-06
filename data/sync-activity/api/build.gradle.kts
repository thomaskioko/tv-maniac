plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.networkUtil.api)
                api(projects.data.connectedAccount.api)
            }
        }
    }
}
