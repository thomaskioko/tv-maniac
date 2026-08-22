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

        androidHostTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.work.testing)
                implementation(projects.core.logger.testing)
            }
        }
    }
}

dependencies {
    "androidHostTestCompilationImplementation"(libs.androidx.junit)
    "androidHostTestCompilationImplementation"(libs.robolectric)
}
