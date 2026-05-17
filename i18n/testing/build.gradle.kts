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
                api(libs.androidx.junit)
                api(projects.i18n.generator)
                implementation(libs.androidx.test.core)
                runtimeOnly(libs.robolectric)
            }
        }

        commonMain {
            dependencies {
                api(libs.moko.resources)
                api(projects.i18n.api)
                api(projects.i18n.implementation)
            }
        }
    }
}
