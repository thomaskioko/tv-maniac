plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil.api)
                api(projects.data.syncActivity.api)
                api(projects.data.syncProvider.api)
            }
        }
    }
}
