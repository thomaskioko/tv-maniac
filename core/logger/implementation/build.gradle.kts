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
                implementation(libs.firebase.crashlytics)
            }
        }

        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.util.api)
            implementation(projects.core.logger.api)
            implementation(projects.data.datastore.api)
            implementation(libs.kermit)
            implementation(libs.napier)
        }
    }
}
