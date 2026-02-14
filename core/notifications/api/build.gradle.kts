plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.datetime)
            }
        }

        androidMain {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
