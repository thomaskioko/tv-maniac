plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.androidx.annotation)
            }
        }
    }
}
