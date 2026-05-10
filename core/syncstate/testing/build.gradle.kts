plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.syncstate.api)

            implementation(libs.coroutines.core)
        }
    }
}
