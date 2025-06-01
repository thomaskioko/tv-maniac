plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.datastore.api)
                implementation(libs.coroutines.core)
            }
        }
    }
}
