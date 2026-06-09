plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                runtimeOnly(libs.coroutines.android)
            }
        }
    }
}
