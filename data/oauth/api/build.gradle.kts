plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.accountManager.api)
                api(libs.coroutines.core)
            }
        }

        androidMain {
            dependencies {
                runtimeOnly(libs.coroutines.android)
            }
        }
    }
}
