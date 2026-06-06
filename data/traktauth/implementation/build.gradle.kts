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
                api(libs.androidx.activity)
                api(libs.androidx.datastore.preference)
                api(libs.appauth)
                api(libs.coroutines.core)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.common)
                implementation(projects.core.networkUtil.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.api.trakt.api)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.datastore.api)
                api(projects.data.requestManager.api)
                api(projects.data.traktauth.api)
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
    }
}
