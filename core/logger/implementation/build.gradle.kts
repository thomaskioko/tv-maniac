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
                api(libs.firebase.crashlytics)
                implementation(libs.kermit.core)
            }
        }

        commonMain.dependencies {
            api(projects.core.appconfig.api)
            api(projects.core.base)
            api(projects.core.logger.api)
            api(projects.data.datastore.api)
            implementation(libs.kermit)
        }
    }
}
