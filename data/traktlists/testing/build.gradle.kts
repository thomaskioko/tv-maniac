plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.traktlists.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
