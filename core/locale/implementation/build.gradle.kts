plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget(
        withDeviceTestBuilder = true,
    )
    useKotlinInject()
}

kotlin {
    sourceSets {
        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.bundles.unittest)
                implementation(libs.androidx.runner)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.locale.api)
                implementation(projects.data.datastore.api)
                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
