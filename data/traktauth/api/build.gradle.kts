plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    multiplatform {
        addAndroidTarget()
    }
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.appauth)
                api(libs.coroutines.core)
            }
        }

        commonMain {
            dependencies {
                api(projects.data.datastore.api)
                api(libs.coroutines.core)
            }
        }
    }
}
