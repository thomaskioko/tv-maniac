plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.api.trakt.api)
                api(projects.core.networkUtil.api)
            }
        }
    }
}
