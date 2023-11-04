plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("plugin.tvmaniac.multiplatform")
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
                api(projects.core.datastore.api)
                api(libs.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth.api"
}
