plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {

            }
        }

        commonMain {
            dependencies {
            }
        }

        iosMain {
            dependencies {

            }
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.common.localization"
}
