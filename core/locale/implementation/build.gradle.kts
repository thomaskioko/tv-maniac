plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget(
        withDeviceTestBuilder = true,
    )
    useMetro()
}

kotlin {
    sourceSets {
        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.androidx.test.core)
                implementation(libs.bundles.unittest)
                implementation(libs.kotlin.test.junit)
                runtimeOnly(libs.androidx.runner)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.locale.api)
                implementation(projects.core.base)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
