plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.base)
            api(libs.coroutines.core)
        }
    }
}
