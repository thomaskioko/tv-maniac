plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.accountManager.api)
                api(projects.data.shows.api)
                api(projects.core.networkUtil.api)
                api(libs.coroutines.core)
            }
        }
    }
}
