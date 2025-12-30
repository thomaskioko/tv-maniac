plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.locale.api)
                implementation(libs.coroutines.core)
            }
        }
    }
}
