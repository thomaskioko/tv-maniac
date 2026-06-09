plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.appconfig.api)
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.oauth.api)
            }
        }
    }
}
