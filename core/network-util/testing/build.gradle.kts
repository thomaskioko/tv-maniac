plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.networkUtil.api)
            implementation(libs.coroutines.core)
        }
    }
}
