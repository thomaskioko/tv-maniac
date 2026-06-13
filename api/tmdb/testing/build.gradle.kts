plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.api.tmdb.api)
                api(projects.core.networkUtil.api)
            }
        }
    }
}
