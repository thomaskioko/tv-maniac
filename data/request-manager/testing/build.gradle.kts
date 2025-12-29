plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.requestManager.api)
            implementation(libs.coroutines.core)
        }
    }
}
