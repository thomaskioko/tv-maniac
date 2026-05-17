plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.syncstate.api)
            api(libs.coroutines.core)
        }
    }
}
