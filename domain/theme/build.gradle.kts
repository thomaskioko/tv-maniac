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

        commonMain {
            dependencies {
                api(projects.i18n.generator)
            }
        }
    }
}
