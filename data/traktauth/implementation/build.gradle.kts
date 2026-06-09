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
                api(libs.appauth)
                api(libs.coroutines.core)
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
                api(projects.data.accountManager.api)
                api(projects.data.oauth.api)
                api(projects.data.traktauth.api)
            }
        }

        jvmMain {
            dependencies {
                api(libs.coroutines.core)
            }
        }
    }
}
