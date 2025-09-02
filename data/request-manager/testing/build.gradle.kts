plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.requestManager.api)
            implementation(libs.coroutines.core)
        }
    }
}
