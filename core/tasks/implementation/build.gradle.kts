plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.androidx.work.runtime)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.tasks.api)
                api(projects.core.logger.api)
                implementation(libs.coroutines.core)
            }
        }

        iosMain {
            dependencies {
                implementation(projects.core.base)
            }
        }
    }
}
