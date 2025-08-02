plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.requestManager.api)
            implementation(libs.coroutines.core)
        }
    }
}
