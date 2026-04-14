plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.tasks.api)
        }
    }
}
