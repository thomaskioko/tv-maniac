plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil.api)
                api(projects.data.accountManager.api)
                api(projects.data.ratings.api)
                api(libs.coroutines.core)
            }
        }
    }
}
