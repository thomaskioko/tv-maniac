plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(projects.i18n.generator)
                runtimeOnly(libs.coroutines.android)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.i18n.generator)
            }
        }
    }
}
