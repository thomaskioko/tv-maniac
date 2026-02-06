plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.upnext.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
