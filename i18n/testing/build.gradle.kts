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
                implementation(libs.androidx.junit)
                implementation(libs.robolectric)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.i18n.api)
                implementation(projects.i18n.implementation)
                implementation(libs.coroutines.core)
                implementation(libs.moko.resources)
            }
        }
    }
}
