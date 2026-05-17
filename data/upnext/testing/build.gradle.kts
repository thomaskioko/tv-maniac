plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.upnext.api)
                api(libs.coroutines.core)
            }
        }
    }
}
