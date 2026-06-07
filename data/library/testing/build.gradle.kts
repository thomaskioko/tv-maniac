plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.library.api)
                api(libs.coroutines.core)
            }
        }
    }
}
