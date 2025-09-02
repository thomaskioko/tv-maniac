plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    explicitApi()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.robolectric)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.i18n.api)
                implementation(libs.coroutines.core)
                implementation(libs.moko.resources)
            }
        }
    }
}
