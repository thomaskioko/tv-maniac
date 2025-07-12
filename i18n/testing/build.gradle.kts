plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
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
