plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.datastore.api)
                api(libs.coroutines.core)
            }
        }
    }
}
