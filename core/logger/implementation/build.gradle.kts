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
            implementation(libs.kermit)
            implementation(libs.kotlinx.atomicfu)
        }

        iosMain.dependencies {
            implementation(libs.crashkios.crashlytics)
        }

        jvmTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.logger.testing)
        }

        iosTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
