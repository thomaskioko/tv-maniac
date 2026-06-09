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
            }
        }

        jvmMain {
            dependencies {
                api(libs.coroutines.core)
            }
        }
    }
}
