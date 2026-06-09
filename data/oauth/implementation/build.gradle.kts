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
                api(libs.androidx.datastore.preference)
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.data.datastore.api)
                api(projects.data.oauth.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.data.accountManager.api)
                api(projects.data.datastore.api)
                api(projects.data.oauth.api)
                api(projects.data.requestManager.api)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.multiplatformsettings.core)
                implementation(libs.multiplatformsettings.coroutines)
            }
        }

        jvmMain {
            dependencies {
                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.oauth.testing)
                implementation(projects.data.requestManager.testing)
            }
        }
    }
}
